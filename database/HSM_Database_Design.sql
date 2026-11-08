-- ============================================================
-- Hola Student Marketplace (HSM) - Physical Database Design
-- DBMS: MySQL 8.0.16+
-- Naming convention: snake_case; InnoDB; utf8mb4
-- Times are stored as DATETIME in Vietnam time (UTC+7, BR.42):
--   run SET GLOBAL time_zone = '+07:00'; (or set it in the connection string)
-- Foreign keys use the default RESTRICT behaviour (no ON UPDATE/ON DELETE
-- clauses), so CHECK constraints and generated columns can use FK columns.
-- ============================================================

DROP DATABASE IF EXISTS hola_student_marketplace;
CREATE DATABASE hola_student_marketplace
  CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE hola_student_marketplace;

-- ============================================================
-- 1. ACCOUNT MANAGEMENT (FE.1)
-- ============================================================

-- users: login account of Buyer/Seller (role USER) and Admin | BR.1-BR.7
CREATE TABLE users (
    user_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,                       -- unique, case-insensitive collation (BR.1)
    password_hash VARCHAR(255) NULL,                   -- NULL for Google-only accounts (BR.3)
    google_subject VARCHAR(255) NULL,
    account_role ENUM('USER','ADMIN') NOT NULL DEFAULT 'USER',      -- BR.7
    account_status ENUM('ACTIVE','LOCKED') NOT NULL DEFAULT 'ACTIVE', -- BR.38
    email_verified_at DATETIME NULL,
    otp_code VARCHAR(10) NULL,                         -- 6-digit OTP (BR.1)
    otp_expires_at DATETIME NULL,                      -- valid 5 minutes
    otp_failed_count TINYINT UNSIGNED NOT NULL DEFAULT 0,
    otp_sent_at DATETIME NULL,                         -- resend at most once / 60 s
    reset_token VARCHAR(255) NULL,                     -- hashed, single use (BR.4)
    reset_token_expires_at DATETIME NULL,              -- valid 15 minutes
    failed_login_count TINYINT UNSIGNED NOT NULL DEFAULT 0,  -- BR.5
    login_locked_until DATETIME NULL,                  -- 15-minute login lock
    last_login_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uq_users_email (email),
    UNIQUE KEY uq_users_google_subject (google_subject),
    KEY idx_users_status_role (account_status, account_role),
    CONSTRAINT chk_users_credential CHECK (password_hash IS NOT NULL OR google_subject IS NOT NULL)
) ENGINE=InnoDB;

-- user_profiles: public profile, rating and ranking | UC.6, UC.7, BR.6, BR.30
-- average_rating and seller_rank are derived from reviews; refresh them when a review is added.
CREATE TABLE user_profiles (
    user_id BIGINT UNSIGNED PRIMARY KEY,
    nickname VARCHAR(80) NOT NULL,
    avatar_url VARCHAR(500) NULL,
    cover_photo_url VARCHAR(500) NULL,
    bio VARCHAR(1000) NULL,
    average_rating DECIMAL(3,2) NOT NULL DEFAULT 0.00,
    seller_rank INT UNSIGNED NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_profiles_user FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT chk_profile_rating CHECK (average_rating BETWEEN 0 AND 5)
) ENGINE=InnoDB;

-- user_sanctions: warnings and locks issued by an Admin | UC.37, BR.38, BR.31
CREATE TABLE user_sanctions (
    sanction_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,                  -- receives
    admin_id BIGINT UNSIGNED NOT NULL,                 -- issues
    sanction_type ENUM('WARNING','LOCK') NOT NULL,
    reason VARCHAR(500) NOT NULL,                      -- reason is required (BR.38)
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    unlocked_at DATETIME NULL,                         -- set when a LOCK is lifted
    CONSTRAINT fk_sanctions_user FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT fk_sanctions_admin FOREIGN KEY (admin_id) REFERENCES users(user_id),
    CONSTRAINT chk_sanction_not_self CHECK (user_id <> admin_id),
    KEY idx_sanctions_user_date (user_id, created_at)
) ENGINE=InnoDB;

-- ============================================================
-- 2. POST MANAGEMENT / PRODUCT DISCOVERY (FE.2, FE.3)
-- ============================================================

-- categories: 7 default categories managed by Admin | BR.8, BR.39
CREATE TABLE categories (
    category_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,           -- hide instead of delete (BR.39)
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_categories_name (category_name)
) ENGINE=InnoDB;

-- areas: faculty/school selected by the seller | BR.17
CREATE TABLE areas (
    area_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    area_name VARCHAR(120) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    UNIQUE KEY uq_areas_name (area_name)
) ENGINE=InnoDB;

-- posts: second-hand product posts | BR.8-BR.15, post state diagrams
CREATE TABLE posts (
    post_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    seller_id BIGINT UNSIGNED NOT NULL,
    category_id BIGINT UNSIGNED NOT NULL,
    area_id BIGINT UNSIGNED NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    reference_price DECIMAL(12,2) NULL,                -- optional; NULL = "Negotiable" (BR.8)
    condition_status ENUM('EXCELLENT','GOOD','FAIR','HEAVILY_USED','NEEDS_REPAIR') NOT NULL, -- BR.8
    approval_status ENUM('PENDING','APPROVED','REJECTED') NOT NULL DEFAULT 'PENDING',
    sale_status ENUM('FOR_SALE','SOLD') NOT NULL DEFAULT 'FOR_SALE',
    display_status ENUM('VISIBLE','HIDDEN','EXPIRED','REMOVED','DELETED') NOT NULL DEFAULT 'VISIBLE',
    rejection_reason VARCHAR(500) NULL,
    approved_by BIGINT UNSIGNED NULL,                  -- Admin who decided (BR.9)
    approved_at DATETIME NULL,
    expires_at DATETIME NULL,                          -- approved_at + post_validity_days (BR.13)
    view_count BIGINT UNSIGNED NOT NULL DEFAULT 0,     -- BR.14
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_posts_seller FOREIGN KEY (seller_id) REFERENCES users(user_id),
    CONSTRAINT fk_posts_category FOREIGN KEY (category_id) REFERENCES categories(category_id),
    CONSTRAINT fk_posts_area FOREIGN KEY (area_id) REFERENCES areas(area_id),
    CONSTRAINT fk_posts_approver FOREIGN KEY (approved_by) REFERENCES users(user_id),
    CONSTRAINT chk_posts_reference_price CHECK (reference_price IS NULL OR reference_price > 0),
    CONSTRAINT chk_posts_rejected CHECK (approval_status <> 'REJECTED' OR rejection_reason IS NOT NULL),
    CONSTRAINT chk_posts_approved CHECK (approval_status <> 'APPROVED'
        OR (approved_at IS NOT NULL AND approved_by IS NOT NULL AND expires_at IS NOT NULL)),
    -- public feed / filters (BR.15, BR.17)
    KEY idx_posts_discovery (approval_status, sale_status, display_status, category_id, area_id, created_at),
    KEY idx_posts_seller (seller_id, created_at),
    KEY idx_posts_expiry (display_status, expires_at),    -- daily job hides expired posts (BR.13)
    FULLTEXT KEY ft_posts_search (title, description)     -- keyword search (BR.16)
) ENGINE=InnoDB;

-- post_images: 1 to 10 images per post | BR.8
CREATE TABLE post_images (
    image_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    post_id BIGINT UNSIGNED NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    display_order TINYINT UNSIGNED NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_post_images_post FOREIGN KEY (post_id) REFERENCES posts(post_id),
    CONSTRAINT chk_post_images_order CHECK (display_order BETWEEN 1 AND 10),   -- max 10 images
    UNIQUE KEY uq_post_image_order (post_id, display_order)
) ENGINE=InnoDB;

-- tags / post_tags: free tags, at most 10 per post (enforce the limit in the service layer) | BR.8
CREATE TABLE tags (
    tag_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    tag_name VARCHAR(60) NOT NULL,
    UNIQUE KEY uq_tags_name (tag_name)
) ENGINE=InnoDB;

CREATE TABLE post_tags (
    post_id BIGINT UNSIGNED NOT NULL,
    tag_id BIGINT UNSIGNED NOT NULL,
    PRIMARY KEY (post_id, tag_id),
    CONSTRAINT fk_post_tags_post FOREIGN KEY (post_id) REFERENCES posts(post_id),
    CONSTRAINT fk_post_tags_tag FOREIGN KEY (tag_id) REFERENCES tags(tag_id),
    KEY idx_post_tags_tag (tag_id)                     -- similar-product suggestion (BR.18)
) ENGINE=InnoDB;

-- post_edits: pending/decided edit of an approved post | BR.11, PostEdit state diagram
CREATE TABLE post_edits (
    edit_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    post_id BIGINT UNSIGNED NOT NULL,
    category_id BIGINT UNSIGNED NOT NULL,              -- proposed category
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    edit_status ENUM('PENDING','APPROVED','REJECTED') NOT NULL DEFAULT 'PENDING',
    rejection_reason VARCHAR(500) NULL,
    reviewed_by BIGINT UNSIGNED NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    reviewed_at DATETIME NULL,
    pending_post_id BIGINT UNSIGNED GENERATED ALWAYS AS
        (CASE WHEN edit_status = 'PENDING' THEN post_id ELSE NULL END) STORED,
    UNIQUE KEY uq_one_pending_edit_per_post (pending_post_id),     -- BR.11
    CONSTRAINT fk_post_edits_post FOREIGN KEY (post_id) REFERENCES posts(post_id),
    CONSTRAINT fk_post_edits_category FOREIGN KEY (category_id) REFERENCES categories(category_id),
    CONSTRAINT fk_post_edits_reviewer FOREIGN KEY (reviewed_by) REFERENCES users(user_id),
    CONSTRAINT chk_post_edits_rejected CHECK (edit_status <> 'REJECTED' OR rejection_reason IS NOT NULL),
    KEY idx_post_edits_queue (edit_status, created_at)             -- Admin queue, oldest first
) ENGINE=InnoDB;

CREATE TABLE post_edit_images (
    edit_image_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    edit_id BIGINT UNSIGNED NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    display_order TINYINT UNSIGNED NOT NULL,
    CONSTRAINT fk_post_edit_images_edit FOREIGN KEY (edit_id) REFERENCES post_edits(edit_id),
    CONSTRAINT chk_post_edit_images_order CHECK (display_order BETWEEN 1 AND 10),
    UNIQUE KEY uq_post_edit_image_order (edit_id, display_order)
) ENGINE=InnoDB;

CREATE TABLE post_edit_tags (
    edit_id BIGINT UNSIGNED NOT NULL,
    tag_id BIGINT UNSIGNED NOT NULL,
    PRIMARY KEY (edit_id, tag_id),
    CONSTRAINT fk_post_edit_tags_edit FOREIGN KEY (edit_id) REFERENCES post_edits(edit_id),
    CONSTRAINT fk_post_edit_tags_tag FOREIGN KEY (tag_id) REFERENCES tags(tag_id)
) ENGINE=InnoDB;

-- search_histories: keywords of logged-in users | UC.17, BR.16
CREATE TABLE search_histories (
    search_history_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    keyword VARCHAR(255) NOT NULL,
    searched_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_search_histories_user FOREIGN KEY (user_id) REFERENCES users(user_id),
    KEY idx_search_user_date (user_id, searched_at)
) ENGINE=InnoDB;

-- viewing_histories: one row per user and product, latest view only | UC.13, BR.14
-- Upsert: INSERT ... ON DUPLICATE KEY UPDATE viewed_at = CURRENT_TIMESTAMP
CREATE TABLE viewing_histories (
    viewing_history_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    post_id BIGINT UNSIGNED NOT NULL,
    viewed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_viewing_user_post (user_id, post_id),            -- BR.14
    CONSTRAINT fk_viewing_histories_user FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT fk_viewing_histories_post FOREIGN KEY (post_id) REFERENCES posts(post_id),
    KEY idx_viewing_user_date (user_id, viewed_at)
) ENGINE=InnoDB;

-- ============================================================
-- 3. COMMUNITY INTERACTION (FE.4)
-- ============================================================

-- conversations: one chat thread between a buyer and the seller of a post | UC.18, BR.19
CREATE TABLE conversations (
    conversation_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    post_id BIGINT UNSIGNED NOT NULL,
    buyer_id BIGINT UNSIGNED NOT NULL,
    seller_id BIGINT UNSIGNED NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_message_at DATETIME NULL,
    UNIQUE KEY uq_conversation_post_buyer (post_id, buyer_id),
    CONSTRAINT fk_conversations_post FOREIGN KEY (post_id) REFERENCES posts(post_id),
    CONSTRAINT fk_conversations_buyer FOREIGN KEY (buyer_id) REFERENCES users(user_id),
    CONSTRAINT fk_conversations_seller FOREIGN KEY (seller_id) REFERENCES users(user_id),
    CONSTRAINT chk_conversation_participants CHECK (buyer_id <> seller_id),
    KEY idx_conversations_buyer (buyer_id, last_message_at),
    KEY idx_conversations_seller (seller_id, last_message_at)
) ENGINE=InnoDB;

CREATE TABLE messages (
    message_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    conversation_id BIGINT UNSIGNED NOT NULL,
    sender_id BIGINT UNSIGNED NOT NULL,
    message_text TEXT NULL,
    image_url VARCHAR(500) NULL,
    is_quick_message BOOLEAN NOT NULL DEFAULT FALSE,   -- BR.19
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    sent_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_messages_conversation FOREIGN KEY (conversation_id) REFERENCES conversations(conversation_id),
    CONSTRAINT fk_messages_sender FOREIGN KEY (sender_id) REFERENCES users(user_id),
    CONSTRAINT chk_messages_body CHECK (message_text IS NOT NULL OR image_url IS NOT NULL),
    KEY idx_messages_conversation_date (conversation_id, sent_at)
) ENGINE=InnoDB;

-- comments: public comments under a post, soft deleted | UC.19, BR.20
CREATE TABLE comments (
    comment_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    post_id BIGINT UNSIGNED NOT NULL,
    user_id BIGINT UNSIGNED NOT NULL,
    comment_content VARCHAR(2000) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_comments_post FOREIGN KEY (post_id) REFERENCES posts(post_id),
    CONSTRAINT fk_comments_user FOREIGN KEY (user_id) REFERENCES users(user_id),
    KEY idx_comments_post_date (post_id, created_at)
) ENGINE=InnoDB;

-- favorites: a user can favorite a post at most once | UC.20, BR.20
CREATE TABLE favorites (
    user_id BIGINT UNSIGNED NOT NULL,
    post_id BIGINT UNSIGNED NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, post_id),
    CONSTRAINT fk_favorites_user FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT fk_favorites_post FOREIGN KEY (post_id) REFERENCES posts(post_id)
) ENGINE=InnoDB;

-- follows: follower -> followed | UC.21, BR.20
CREATE TABLE follows (
    follower_id BIGINT UNSIGNED NOT NULL,
    followed_id BIGINT UNSIGNED NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (follower_id, followed_id),
    CONSTRAINT fk_follows_follower FOREIGN KEY (follower_id) REFERENCES users(user_id),
    CONSTRAINT fk_follows_followed FOREIGN KEY (followed_id) REFERENCES users(user_id),
    CONSTRAINT chk_no_self_follow CHECK (follower_id <> followed_id),
    KEY idx_follows_followed (followed_id)
) ENGINE=InnoDB;

-- violation_reports: reports of posts that break the rules | UC.23, UC.35, BR.21
CREATE TABLE violation_reports (
    report_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    post_id BIGINT UNSIGNED NOT NULL,
    reporter_id BIGINT UNSIGNED NOT NULL,
    report_reason VARCHAR(1000) NOT NULL,              -- reason is required (BR.21)
    report_status ENUM('PENDING','POST_REMOVED','DISMISSED') NOT NULL DEFAULT 'PENDING',
    admin_id BIGINT UNSIGNED NULL,
    admin_note VARCHAR(1000) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resolved_at DATETIME NULL,
    UNIQUE KEY uq_reporter_post (reporter_id, post_id),            -- report a post at most once
    CONSTRAINT fk_reports_post FOREIGN KEY (post_id) REFERENCES posts(post_id),
    CONSTRAINT fk_reports_reporter FOREIGN KEY (reporter_id) REFERENCES users(user_id),
    CONSTRAINT fk_reports_admin FOREIGN KEY (admin_id) REFERENCES users(user_id),
    CONSTRAINT chk_reports_resolved CHECK (report_status = 'PENDING' OR resolved_at IS NOT NULL),
    KEY idx_reports_status_date (report_status, created_at)
) ENGINE=InnoDB;

-- notifications: in-app notifications | UC.22, BR.22
CREATE TABLE notifications (
    notification_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    notification_type ENUM('NEW_MESSAGE','OFFER_RECEIVED','OFFER_RESPONDED','POST_APPROVED','POST_REJECTED',
        'POST_EDIT_REVIEWED','ORDER_STATUS','COMPLAINT_RESULT','BOOST_PAYMENT_SUCCESS','BOOST_PAYMENT_FAILED',
        'BOOST_EXPIRED','ACCOUNT_SANCTION','SYSTEM') NOT NULL,
    title VARCHAR(200) NOT NULL,
    content VARCHAR(1000) NOT NULL,
    related_entity_type VARCHAR(50) NULL,
    related_entity_id BIGINT UNSIGNED NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users(user_id),
    KEY idx_notifications_user_read_date (user_id, is_read, created_at)
) ENGINE=InnoDB;

-- ============================================================
-- 4. TRADING (FE.5, UC.24 - UC.30)
-- ============================================================

-- price_offers: price offer and negotiation (max 3 rounds) | UC.24, UC.25, BR.24, BR.25
CREATE TABLE price_offers (
    offer_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    post_id BIGINT UNSIGNED NOT NULL,
    buyer_id BIGINT UNSIGNED NOT NULL,
    seller_id BIGINT UNSIGNED NOT NULL,
    proposed_price DECIMAL(12,2) NOT NULL,
    counter_price DECIMAL(12,2) NULL,
    agreed_price DECIMAL(12,2) NULL,
    negotiation_rounds TINYINT UNSIGNED NOT NULL DEFAULT 1,
    offer_status ENUM('AWAITING_RESPONSE','COUNTER_PENDING','ACCEPTED','REJECTED','CANCELLED','EXPIRED','VOID')
        NOT NULL DEFAULT 'AWAITING_RESPONSE',
    sent_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at DATETIME NOT NULL,                      -- sent_at + offer period (BR.24)
    counter_sent_at DATETIME NULL,
    counter_expires_at DATETIME NULL,
    responded_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    active_buyer_id BIGINT UNSIGNED GENERATED ALWAYS AS
        (CASE WHEN offer_status IN ('AWAITING_RESPONSE','COUNTER_PENDING') THEN buyer_id ELSE NULL END) STORED,
    active_post_id BIGINT UNSIGNED GENERATED ALWAYS AS
        (CASE WHEN offer_status IN ('AWAITING_RESPONSE','COUNTER_PENDING') THEN post_id ELSE NULL END) STORED,
    CONSTRAINT fk_price_offers_post FOREIGN KEY (post_id) REFERENCES posts(post_id),
    CONSTRAINT fk_price_offers_buyer FOREIGN KEY (buyer_id) REFERENCES users(user_id),
    CONSTRAINT fk_price_offers_seller FOREIGN KEY (seller_id) REFERENCES users(user_id),
    CONSTRAINT chk_offer_positive_price CHECK (proposed_price > 0),
    CONSTRAINT chk_offer_counter_price CHECK (counter_price IS NULL OR counter_price > 0),
    CONSTRAINT chk_offer_rounds CHECK (negotiation_rounds BETWEEN 1 AND 3),
    CONSTRAINT chk_offer_parties CHECK (buyer_id <> seller_id),
    UNIQUE KEY uq_one_active_offer_per_buyer_post (active_buyer_id, active_post_id),   -- BR.24
    KEY idx_offers_seller_status (seller_id, offer_status, sent_at),
    KEY idx_offers_expiry (offer_status, expires_at)   -- hourly expiry job
) ENGINE=InnoDB;

CREATE TABLE price_offer_status_histories (
    offer_history_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    offer_id BIGINT UNSIGNED NOT NULL,
    old_status VARCHAR(30) NULL,
    new_status VARCHAR(30) NOT NULL,
    changed_by BIGINT UNSIGNED NULL,
    change_reason VARCHAR(500) NULL,
    changed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_offer_history_offer FOREIGN KEY (offer_id) REFERENCES price_offers(offer_id),
    CONSTRAINT fk_offer_history_user FOREIGN KEY (changed_by) REFERENCES users(user_id),
    KEY idx_offer_history_date (offer_id, changed_at)
) ENGINE=InnoDB;

-- orders: Buy now or order from an accepted offer | UC.26-UC.28, BR.26-BR.29, BR.31, BR.32
-- final_price may be NULL for a Buy now on a post without reference price (price agreed outside the system).
CREATE TABLE orders (
    order_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    order_code VARCHAR(30) NOT NULL,
    post_id BIGINT UNSIGNED NOT NULL,
    buyer_id BIGINT UNSIGNED NOT NULL,
    seller_id BIGINT UNSIGNED NOT NULL,
    accepted_offer_id BIGINT UNSIGNED NULL,
    order_source ENUM('BUY_NOW','ACCEPTED_OFFER') NOT NULL DEFAULT 'BUY_NOW',
    final_price DECIMAL(12,2) NULL,
    receiving_method VARCHAR(255) NULL,                -- descriptive only (BR.26)
    payment_method VARCHAR(255) NULL,                  -- descriptive only (BR.26)
    order_status ENUM('PENDING_CONFIRMATION','CONFIRMED','COMPLETED','CANCELLED')
        NOT NULL DEFAULT 'PENDING_CONFIRMATION',
    is_frozen BOOLEAN NOT NULL DEFAULT FALSE,          -- flag on a CONFIRMED order (BR.32)
    frozen_at DATETIME NULL,
    frozen_reason VARCHAR(500) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    seller_confirmed_at DATETIME NULL,
    confirmation_deadline_at DATETIME NOT NULL,        -- created_at + 24 h (BR.27)
    complaint_deadline_at DATETIME NULL,               -- seller_confirmed_at + 7 days, fixed (BR.29)
    completed_at DATETIME NULL,
    cancelled_at DATETIME NULL,
    cancellation_reason VARCHAR(500) NULL,
    active_post_id BIGINT UNSIGNED GENERATED ALWAYS AS
        (CASE WHEN order_status IN ('PENDING_CONFIRMATION','CONFIRMED') THEN post_id ELSE NULL END) STORED,
    UNIQUE KEY uq_orders_code (order_code),
    UNIQUE KEY uq_one_active_order_per_post (active_post_id),      -- BR.26
    UNIQUE KEY uq_orders_accepted_offer (accepted_offer_id),
    CONSTRAINT fk_orders_post FOREIGN KEY (post_id) REFERENCES posts(post_id),
    CONSTRAINT fk_orders_buyer FOREIGN KEY (buyer_id) REFERENCES users(user_id),
    CONSTRAINT fk_orders_seller FOREIGN KEY (seller_id) REFERENCES users(user_id),
    CONSTRAINT fk_orders_offer FOREIGN KEY (accepted_offer_id) REFERENCES price_offers(offer_id),
    CONSTRAINT chk_order_parties CHECK (buyer_id <> seller_id),
    CONSTRAINT chk_order_price CHECK (final_price IS NULL OR final_price >= 0),
    CONSTRAINT chk_order_confirmed CHECK (order_status NOT IN ('CONFIRMED','COMPLETED')
        OR (seller_confirmed_at IS NOT NULL AND complaint_deadline_at IS NOT NULL)),
    CONSTRAINT chk_order_frozen CHECK (is_frozen = FALSE
        OR (order_status = 'CONFIRMED' AND frozen_at IS NOT NULL)),   -- BR.32
    CONSTRAINT chk_order_cancelled CHECK (order_status <> 'CANCELLED' OR cancelled_at IS NOT NULL),
    KEY idx_orders_buyer_status (buyer_id, order_status, created_at),
    KEY idx_orders_seller_status (seller_id, order_status, created_at),
    KEY idx_orders_auto_cancel (order_status, confirmation_deadline_at),
    KEY idx_orders_auto_complete (order_status, complaint_deadline_at, is_frozen)
) ENGINE=InnoDB;

-- order_status_histories: every status change is stored | BR.31, BR.43
CREATE TABLE order_status_histories (
    order_history_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT UNSIGNED NOT NULL,
    old_status VARCHAR(30) NULL,
    new_status VARCHAR(30) NOT NULL,
    changed_by BIGINT UNSIGNED NULL,
    change_source ENUM('USER','SYSTEM_JOB','ADMIN','ACCOUNT_LOCK') NOT NULL DEFAULT 'USER',
    change_reason VARCHAR(500) NULL,
    changed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_order_history_order FOREIGN KEY (order_id) REFERENCES orders(order_id),
    CONSTRAINT fk_order_history_user FOREIGN KEY (changed_by) REFERENCES users(user_id),
    KEY idx_order_history_date (order_id, changed_at)
) ENGINE=InnoDB;

-- complaints: complaint about an order, decided by Admin | UC.29, UC.36, BR.29
CREATE TABLE complaints (
    complaint_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT UNSIGNED NOT NULL,
    filed_by BIGINT UNSIGNED NOT NULL,                 -- buyer or seller
    complaint_content TEXT NOT NULL,                   -- required (BR.29)
    complaint_status ENUM('PENDING','RESOLVED') NOT NULL DEFAULT 'PENDING',
    admin_id BIGINT UNSIGNED NULL,
    admin_decision VARCHAR(1000) NULL,
    filed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    decided_at DATETIME NULL,
    active_order_id BIGINT UNSIGNED GENERATED ALWAYS AS
        (CASE WHEN complaint_status = 'PENDING' THEN order_id ELSE NULL END) STORED,
    UNIQUE KEY uq_one_open_complaint_per_order (active_order_id),  -- BR.29
    CONSTRAINT fk_complaints_order FOREIGN KEY (order_id) REFERENCES orders(order_id),
    CONSTRAINT fk_complaints_filer FOREIGN KEY (filed_by) REFERENCES users(user_id),
    CONSTRAINT fk_complaints_admin FOREIGN KEY (admin_id) REFERENCES users(user_id),
    CONSTRAINT chk_complaints_resolved CHECK (complaint_status = 'PENDING'
        OR (admin_id IS NOT NULL AND admin_decision IS NOT NULL AND decided_at IS NOT NULL)),
    KEY idx_complaints_status_date (complaint_status, filed_at)
) ENGINE=InnoDB;

CREATE TABLE complaint_evidences (
    evidence_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    complaint_id BIGINT UNSIGNED NOT NULL,
    evidence_url VARCHAR(500) NOT NULL,
    uploaded_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_complaint_evidence FOREIGN KEY (complaint_id) REFERENCES complaints(complaint_id),
    KEY idx_evidence_complaint (complaint_id)
) ENGINE=InnoDB;

CREATE TABLE complaint_status_histories (
    complaint_history_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    complaint_id BIGINT UNSIGNED NOT NULL,
    old_status VARCHAR(30) NULL,
    new_status VARCHAR(30) NOT NULL,
    changed_by BIGINT UNSIGNED NULL,
    note VARCHAR(1000) NULL,
    changed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_complaint_history_complaint FOREIGN KEY (complaint_id) REFERENCES complaints(complaint_id),
    CONSTRAINT fk_complaint_history_user FOREIGN KEY (changed_by) REFERENCES users(user_id),
    KEY idx_complaint_history_date (complaint_id, changed_at)
) ENGINE=InnoDB;

-- reviews: one review per completed order, buyer -> seller | UC.30, BR.30
CREATE TABLE reviews (
    review_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT UNSIGNED NOT NULL,
    buyer_id BIGINT UNSIGNED NOT NULL,
    seller_id BIGINT UNSIGNED NOT NULL,
    rating TINYINT UNSIGNED NOT NULL,
    review_comment VARCHAR(2000) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_review_per_order (order_id),
    CONSTRAINT fk_reviews_order FOREIGN KEY (order_id) REFERENCES orders(order_id),
    CONSTRAINT fk_reviews_buyer FOREIGN KEY (buyer_id) REFERENCES users(user_id),
    CONSTRAINT fk_reviews_seller FOREIGN KEY (seller_id) REFERENCES users(user_id),
    CONSTRAINT chk_review_rating CHECK (rating BETWEEN 1 AND 5),
    CONSTRAINT chk_review_parties CHECK (buyer_id <> seller_id),
    KEY idx_reviews_seller_date (seller_id, created_at)
) ENGINE=InnoDB;

-- ============================================================
-- 5. POST BOOST / PAYMENT (FE.6, UC.31 - UC.33, UC.38, UC.39)
-- ============================================================

-- boost_packages: 1, 7, 30 days; price configured by Admin | BR.33, BR.39
CREATE TABLE boost_packages (
    package_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    package_name VARCHAR(80) NOT NULL,
    duration_days SMALLINT UNSIGNED NOT NULL,
    price DECIMAL(12,2) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_boost_duration CHECK (duration_days IN (1,7,30)),
    CONSTRAINT chk_boost_price CHECK (price >= 0),
    UNIQUE KEY uq_boost_package_duration (duration_days)
) ENGINE=InnoDB;

-- receiving_bank_accounts: Admin accounts used for QR codes, only one enabled | UC.39, BR.34
-- Accounts are never deleted once used (FK RESTRICT); disable them instead.
CREATE TABLE receiving_bank_accounts (
    bank_account_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    bank_name VARCHAR(100) NOT NULL,
    account_number VARCHAR(50) NOT NULL,
    account_holder VARCHAR(150) NOT NULL,
    is_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    created_by BIGINT UNSIGNED NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    enabled_flag TINYINT GENERATED ALWAYS AS (CASE WHEN is_enabled THEN 1 ELSE NULL END) STORED,
    UNIQUE KEY uq_bank_account_number (account_number),
    UNIQUE KEY uq_only_one_enabled_account (enabled_flag),         -- BR.34
    CONSTRAINT fk_bank_accounts_admin FOREIGN KEY (created_by) REFERENCES users(user_id)
) ENGINE=InnoDB;

-- boost_transactions: QR payment info, payment status and boost period | BR.33-BR.37, BoostTransaction state diagram
CREATE TABLE boost_transactions (
    boost_transaction_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    transaction_code VARCHAR(50) NOT NULL,             -- unique transfer description (BR.34)
    seller_id BIGINT UNSIGNED NOT NULL,
    post_id BIGINT UNSIGNED NOT NULL,
    package_id BIGINT UNSIGNED NOT NULL,
    bank_account_id BIGINT UNSIGNED NOT NULL,
    amount DECIMAL(12,2) NOT NULL,                     -- price locked at creation (BR.33)
    qr_content TEXT NOT NULL,
    payment_status ENUM('PENDING_PAYMENT','SUCCESSFUL','EXPIRED','LATE_SUCCESSFUL',
        'PENDING_RECONCILIATION','RECONCILED') NOT NULL DEFAULT 'PENDING_PAYMENT',   -- BR.35
    boost_status ENUM('PENDING_PAYMENT','ACTIVE','EXPIRED') NOT NULL DEFAULT 'PENDING_PAYMENT',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    payment_deadline_at DATETIME NOT NULL,             -- created_at + 24 h
    paid_at DATETIME NULL,
    boost_starts_at DATETIME NULL,
    boost_ends_at DATETIME NULL,
    view_count INT UNSIGNED NOT NULL DEFAULT 0,        -- UC.33
    interaction_count INT UNSIGNED NOT NULL DEFAULT 0, -- UC.33
    active_post_id BIGINT UNSIGNED GENERATED ALWAYS AS
        (CASE WHEN boost_status = 'ACTIVE' THEN post_id ELSE NULL END) STORED,
    UNIQUE KEY uq_boost_transaction_code (transaction_code),
    UNIQUE KEY uq_one_active_boost_per_post (active_post_id),      -- BR.33
    CONSTRAINT fk_boost_tx_seller FOREIGN KEY (seller_id) REFERENCES users(user_id),
    CONSTRAINT fk_boost_tx_post FOREIGN KEY (post_id) REFERENCES posts(post_id),
    CONSTRAINT fk_boost_tx_package FOREIGN KEY (package_id) REFERENCES boost_packages(package_id),
    CONSTRAINT fk_boost_tx_bank FOREIGN KEY (bank_account_id) REFERENCES receiving_bank_accounts(bank_account_id),
    CONSTRAINT chk_boost_tx_amount CHECK (amount >= 0),
    CONSTRAINT chk_boost_tx_paid CHECK (payment_status NOT IN ('SUCCESSFUL','LATE_SUCCESSFUL') OR paid_at IS NOT NULL),
    CONSTRAINT chk_boost_tx_active CHECK (boost_status <> 'ACTIVE'
        OR (boost_starts_at IS NOT NULL AND boost_ends_at IS NOT NULL)),
    KEY idx_boost_tx_status_deadline (payment_status, payment_deadline_at),  -- hourly expiry job
    KEY idx_boost_tx_end (boost_status, boost_ends_at),                      -- hourly package expiry job
    KEY idx_boost_tx_seller_date (seller_id, created_at)
) ENGINE=InnoDB;

-- incoming_funds: every incoming payment reported by the Payment Gateway | UC.38, BR.36, IncomingFund state diagram
CREATE TABLE incoming_funds (
    fund_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    bank_account_id BIGINT UNSIGNED NOT NULL,
    boost_transaction_id BIGINT UNSIGNED NULL,         -- set when the fund is matched
    reconciled_by BIGINT UNSIGNED NULL,
    amount DECIMAL(12,2) NOT NULL,
    received_at DATETIME NOT NULL,
    transfer_description VARCHAR(255) NULL,
    gateway_ref_code VARCHAR(100) NOT NULL,            -- prevents duplicates (NFR 17, BR.36)
    sender_name VARCHAR(150) NULL,
    fund_status ENUM('UNIDENTIFIED','MATCHED','RECONCILED') NOT NULL DEFAULT 'UNIDENTIFIED',
    resolution ENUM('ASSIGNED','REFUNDED') NULL,
    refund_reference VARCHAR(100) NULL,
    reconciled_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_incoming_fund_ref (gateway_ref_code),
    CONSTRAINT fk_funds_bank FOREIGN KEY (bank_account_id) REFERENCES receiving_bank_accounts(bank_account_id),
    CONSTRAINT fk_funds_boost_tx FOREIGN KEY (boost_transaction_id) REFERENCES boost_transactions(boost_transaction_id),
    CONSTRAINT fk_funds_admin FOREIGN KEY (reconciled_by) REFERENCES users(user_id),
    CONSTRAINT chk_fund_amount CHECK (amount > 0),
    CONSTRAINT chk_fund_reconciled CHECK (fund_status <> 'RECONCILED'
        OR (resolution IS NOT NULL AND reconciled_by IS NOT NULL AND reconciled_at IS NOT NULL)),
    KEY idx_funds_status_date (fund_status, received_at),
    KEY idx_funds_boost_tx (boost_transaction_id)
) ENGINE=InnoDB;

-- ============================================================
-- 6. ADMINISTRATION / AUDIT (FE.7)
-- ============================================================

-- admin_action_logs: every Admin action (who, what, when), kept 12 months | NFR 24
CREATE TABLE admin_action_logs (
    admin_action_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    admin_id BIGINT UNSIGNED NOT NULL,
    target_type VARCHAR(50) NOT NULL,
    target_id BIGINT UNSIGNED NOT NULL,
    action_type VARCHAR(80) NOT NULL,
    action_reason VARCHAR(1000) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_admin_logs_admin FOREIGN KEY (admin_id) REFERENCES users(user_id),
    KEY idx_admin_logs_target (target_type, target_id),
    KEY idx_admin_logs_admin_date (admin_id, created_at)
) ENGINE=InnoDB;

-- system_settings: Admin-configurable values | UC.40, BR.39, NFR 30
CREATE TABLE system_settings (
    setting_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    setting_key VARCHAR(100) NOT NULL,
    setting_value VARCHAR(500) NOT NULL,
    description VARCHAR(500) NULL,
    updated_by BIGINT UNSIGNED NULL,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uq_system_settings_key (setting_key),
    CONSTRAINT fk_settings_admin FOREIGN KEY (updated_by) REFERENCES users(user_id)
) ENGINE=InnoDB;

-- ============================================================
-- SEED DATA
-- ============================================================
INSERT INTO categories (category_name) VALUES
 ('Books'),('Electronics'),('Clothing'),('Furniture'),('Stationery'),('Sports'),('Other');

-- post_validity_days = 30 is an assumed default (BR.13 only says "X days"); change if the team decides otherwise.
INSERT INTO system_settings (setting_key, setting_value, description) VALUES
 ('post_validity_days','30','Days a post stays public after approval (BR.13)'),
 ('order_auto_complete_days','7','Auto-complete and complaint period of an order (BR.28, BR.29)'),
 ('offer_expiry_days','7','Days before a price offer or counter price expires (BR.24, BR.25)');

-- Example prices in VND; replace with the real prices (BR.39).
INSERT INTO boost_packages (package_name, duration_days, price) VALUES
 ('Boost 1 day',1,10000),('Boost 7 days',7,50000),('Boost 30 days',30,150000);

-- ============================================================
-- Index rationale
-- * posts: discovery by status/category/area/newest, daily expiry job, full-text search.
-- * post_edits: unique pending edit per post and Admin queue.
-- * price_offers: seller inbox, one active offer per buyer-post, hourly expiry job.
-- * orders: buyer/seller lists, 24-hour auto-cancel and 7-day auto-complete jobs.
-- * complaints / violation_reports: Admin queues by status and date.
-- * reviews: rating aggregation by seller_id.
-- * notifications: unread list ordered by creation time.
-- * boost_transactions / incoming_funds: payment expiry, package expiry, reconciliation.
--
-- Rules to enforce in the service layer (inside a DB transaction):
-- 1. Order buyer is not the post seller; post is approved, FOR_SALE and VISIBLE (BR.26).
-- 2. A review is allowed only for a COMPLETED order, by its buyer (BR.30).
-- 3. A post has at most 10 tags (BR.8). Reporter is not the post owner (BR.21).
-- 4. Set orders.complaint_deadline_at once when the seller confirms
--    (seller_confirmed_at + order_auto_complete_days). Never recalculate it (BR.29),
--    except the single extension of a frozen order (BR.32).
-- 5. HSM does not process or hold trade payments; payment_method and
--    receiving_method are descriptive only. Order, payment and complaint rows are
--    never physically deleted (BR.43, NFR 29).
-- ============================================================