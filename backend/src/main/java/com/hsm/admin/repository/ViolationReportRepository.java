package com.hsm.admin.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.hsm.admin.entity.ViolationReportRecord;

import java.util.List;
import java.util.Map;

/** SQL persistence for the ViolationReport subsystem. Schema: database/HSM_Database_Design.sql. */
public interface ViolationReportRepository extends JpaRepository<ViolationReportRecord, Long>, AdminJpaOperations {
  public static final String SQL_01="SELECT r.*,p.title FROM violation_reports r JOIN posts p ON p.post_id=r.post_id WHERE (?='ALL' OR r.report_status=?) ORDER BY r.created_at DESC";
  public static final String SQL_02="SELECT r.*,p.seller_id FROM violation_reports r JOIN posts p ON p.post_id=r.post_id WHERE report_id=? FOR UPDATE";
  public static final String SQL_03="UPDATE posts SET display_status='REMOVED' WHERE post_id=?";
  public static final String SQL_04="UPDATE violation_reports SET report_status=?,admin_id=?,admin_note=?,resolved_at=NOW() WHERE report_id=?";
  default List<Map<String,Object>> findReports(String status){return queryForList(SQL_01,status,status);}

}
