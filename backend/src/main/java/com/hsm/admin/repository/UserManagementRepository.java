package com.hsm.admin.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.hsm.admin.entity.UserSanctionRecord;

import java.util.List;
import java.util.Map;

/** SQL persistence for the UserManagement subsystem. Schema: database/HSM_Database_Design.sql. */
public interface UserManagementRepository extends JpaRepository<UserSanctionRecord, Long>, AdminJpaOperations {
  public static final String SQL_01="SELECT user_id AS id,email,account_role AS role,account_status AS status,created_at FROM users WHERE email LIKE ? OR CAST(user_id AS CHAR) LIKE ? ORDER BY user_id DESC LIMIT 100";
  public static final String SQL_02="UPDATE users SET account_status='LOCKED' WHERE user_id=?";
  public static final String SQL_03="INSERT INTO user_sanctions(user_id,admin_id,sanction_type,reason) VALUES(?,?,'LOCK',?)";
  public static final String SQL_04="SELECT order_id,order_status,buyer_id,seller_id FROM orders WHERE (buyer_id=? OR seller_id=?) AND order_status='PENDING_CONFIRMATION' FOR UPDATE";
  public static final String SQL_05="UPDATE orders SET order_status='CANCELLED',cancelled_at=NOW(),cancellation_reason='account locked' WHERE order_id=?";
  public static final String SQL_06="SELECT order_id,order_status,buyer_id,seller_id,complaint_deadline_at FROM orders WHERE (buyer_id=? OR seller_id=?) AND order_status='CONFIRMED' AND is_frozen=FALSE FOR UPDATE";
  public static final String SQL_07="UPDATE orders SET is_frozen=TRUE,frozen_at=NOW(),frozen_reason='account locked',complaint_deadline_at=GREATEST(complaint_deadline_at,DATE_ADD(NOW(), INTERVAL 7 DAY)) WHERE order_id=?";
  public static final String SQL_08="SELECT offer_id,offer_status,buyer_id,seller_id FROM price_offers WHERE (buyer_id=? OR seller_id=?) AND offer_status IN ('AWAITING_RESPONSE','COUNTER_PENDING') FOR UPDATE";
  public static final String SQL_09="SELECT offer_id,offer_status,buyer_id,seller_id FROM price_offers WHERE (buyer_id=? OR seller_id=?) AND offer_status='ACCEPTED' AND NOT EXISTS (SELECT 1 FROM orders WHERE accepted_offer_id=price_offers.offer_id) FOR UPDATE";
  public static final String SQL_10="UPDATE posts SET display_status='HIDDEN' WHERE seller_id=? AND display_status='VISIBLE'";
  public static final String SQL_11="UPDATE users SET account_status='ACTIVE' WHERE user_id=?";
  public static final String SQL_12="UPDATE user_sanctions SET unlocked_at=NOW() WHERE user_id=? AND sanction_type='LOCK' AND unlocked_at IS NULL";
  public static final String SQL_13="SELECT order_id FROM orders WHERE (buyer_id=? OR seller_id=?) AND order_status='CONFIRMED' AND is_frozen=TRUE FOR UPDATE";
  public static final String SQL_14="SELECT COUNT(*) FROM orders o JOIN users b ON b.user_id=o.buyer_id JOIN users s ON s.user_id=o.seller_id WHERE o.order_id=? AND (b.account_status='LOCKED' OR s.account_status='LOCKED')";
  public static final String SQL_15="UPDATE orders SET is_frozen=FALSE,frozen_at=NULL,frozen_reason=NULL WHERE order_id=?";
  public static final String SQL_16="UPDATE posts SET display_status='VISIBLE' WHERE seller_id=? AND approval_status='APPROVED' AND sale_status='FOR_SALE' AND display_status='HIDDEN' AND expires_at>NOW()";
  public static final String SQL_17="INSERT INTO user_sanctions(user_id,admin_id,sanction_type,reason) VALUES(?,?,'WARNING',?)";
  public static final String SQL_18="UPDATE price_offers SET offer_status=? WHERE offer_id=?";
  public static final String SQL_19="INSERT INTO price_offer_status_histories(offer_id,old_status,new_status,changed_by,change_reason) VALUES(?,?,?,?,?)";
  public static final String SQL_20="SELECT * FROM orders WHERE (buyer_id=? OR seller_id=?) AND is_frozen=TRUE ORDER BY frozen_at DESC";
  public static final String SQL_21="SELECT * FROM orders WHERE order_id=? FOR UPDATE";
  public static final String SQL_22="UPDATE orders SET order_status='COMPLETED',is_frozen=FALSE,completed_at=NOW() WHERE order_id=?";
  public static final String SQL_23="UPDATE posts SET sale_status='SOLD',display_status='HIDDEN' WHERE post_id=?";
  public static final String SQL_24="UPDATE orders SET order_status='CANCELLED',is_frozen=FALSE,cancelled_at=NOW(),cancellation_reason=? WHERE order_id=?";
  default List<Map<String,Object>> searchUsers(String search){return queryForList(SQL_01,search,search);}

}
