package com.hsm.admin.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.hsm.admin.entity.ComplaintRecord;

import java.util.List;
import java.util.Map;

/** SQL persistence for the Complaint subsystem. Schema: database/HSM_Database_Design.sql. */
public interface ComplaintRepository extends JpaRepository<ComplaintRecord, Long>, AdminJpaOperations {
  public static final String SQL_01="SELECT c.*,o.buyer_id,o.seller_id,o.order_status,o.is_frozen FROM complaints c JOIN orders o ON o.order_id=c.order_id WHERE (?='ALL' OR c.complaint_status=?) ORDER BY c.filed_at DESC";
  public static final String SQL_02="SELECT c.*,o.buyer_id,o.seller_id,o.order_status,o.is_frozen FROM complaints c JOIN orders o ON o.order_id=c.order_id WHERE c.complaint_id=? FOR UPDATE";
  public static final String SQL_03="UPDATE complaints SET complaint_status='RESOLVED',admin_id=?,admin_decision=?,decided_at=NOW() WHERE complaint_id=?";
  public static final String SQL_04="INSERT INTO complaint_status_histories(complaint_id,old_status,new_status,changed_by,note) VALUES(?,'PENDING','RESOLVED',?,?)";
  default List<Map<String,Object>> findComplaints(String status){return queryForList(SQL_01,status,status);}

}
