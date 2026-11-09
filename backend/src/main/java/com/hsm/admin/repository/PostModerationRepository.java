package com.hsm.admin.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.hsm.admin.entity.PostModerationRecord;

import java.util.List;
import java.util.Map;

/** SQL persistence for the PostModeration subsystem. Schema: database/HSM_Database_Design.sql. */
public interface PostModerationRepository extends JpaRepository<PostModerationRecord, Long>, AdminJpaOperations {
  public static final String SQL_01="SELECT p.post_id AS id,p.title,p.seller_id,p.created_at,p.approval_status FROM posts p WHERE p.approval_status='PENDING' ORDER BY p.created_at";
  public static final String SQL_02="SELECT e.edit_id AS id,e.post_id,e.title,e.description,e.category_id,e.created_at FROM post_edits e WHERE e.edit_status='PENDING' ORDER BY e.created_at";
  public static final String SQL_03="SELECT post_id,seller_id,approval_status FROM posts WHERE post_id=? FOR UPDATE";
  public static final String SQL_04="SELECT CAST(setting_value AS UNSIGNED) FROM system_settings WHERE setting_key='post_validity_days'";
  public static final String SQL_05="UPDATE posts SET approval_status='APPROVED',display_status='VISIBLE',rejection_reason=NULL,approved_by=?,approved_at=NOW(),expires_at=DATE_ADD(NOW(), INTERVAL ? DAY) WHERE post_id=?";
  public static final String SQL_06="UPDATE posts SET approval_status='REJECTED',display_status='HIDDEN',rejection_reason=?,approved_by=? WHERE post_id=?";
  public static final String SQL_07="SELECT edit_id,post_id,title,description,category_id,edit_status FROM post_edits WHERE edit_id=? FOR UPDATE";
  public static final String SQL_08="UPDATE posts SET title=?,description=?,category_id=? WHERE post_id=?";
  public static final String SQL_09="DELETE FROM post_images WHERE post_id=?";
  public static final String SQL_10="INSERT INTO post_images(post_id,image_url,display_order) SELECT ?,image_url,display_order FROM post_edit_images WHERE edit_id=?";
  public static final String SQL_11="DELETE FROM post_tags WHERE post_id=?";
  public static final String SQL_12="INSERT INTO post_tags(post_id,tag_id) SELECT ?,tag_id FROM post_edit_tags WHERE edit_id=?";
  public static final String SQL_13="UPDATE post_edits SET edit_status=?,rejection_reason=?,reviewed_by=?,reviewed_at=NOW() WHERE edit_id=?";
  public static final String SQL_14="SELECT seller_id FROM posts WHERE post_id=?";
  default List<Map<String,Object>> findPendingPosts(){return queryForList(SQL_01);}
  default List<Map<String,Object>> findPendingEdits(){return queryForList(SQL_02);}

}
