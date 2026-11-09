package com.hsm.admin.repository;
import java.util.List;
import java.util.Map;
/** Spring Data JPA custom repository fragment for existing SQL queries that span several tables. */
public interface AdminJpaOperations {
  String SHARED_SQL_01="INSERT INTO admin_action_logs(admin_id,target_type,target_id,action_type,action_reason) VALUES(?,?,?,?,?)";
  String SHARED_SQL_02="INSERT INTO notifications(user_id,notification_type,title,content,related_entity_type,related_entity_id) VALUES(?,?,?,?,?,?)";
  String SHARED_SQL_03="SELECT email FROM users WHERE user_id=?";
  String SHARED_SQL_04="INSERT INTO order_status_histories(order_id,old_status,new_status,changed_by,change_source,change_reason) VALUES(?,?,?,?,?,?)";
  List<Map<String,Object>> queryForList(String sql, Object... args);
  int update(String sql, Object... args);
  <T> T queryForObject(String sql, Class<T> type, Object... args);
}
