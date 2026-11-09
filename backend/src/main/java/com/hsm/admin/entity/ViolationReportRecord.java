package com.hsm.admin.entity;
import jakarta.persistence.*;
/** Read model mapped to the existing MySQL table; multi-table writes use native JPA queries within transactional services. */
@Entity
@org.hibernate.annotations.Immutable
@Table(name="violation_reports")
public class ViolationReportRecord {
  public enum ReportStatus { PENDING, POST_REMOVED, DISMISSED }
  @Id @Column(name="report_id") private Long id;
  @Column(name="post_id") private Long postId;
  @Column(name="reporter_id") private Long reporterId;
  @Enumerated(EnumType.STRING)
  @Column(name="report_status", nullable=false) private ReportStatus reportStatus;
  protected ViolationReportRecord() {}
  public Long getId(){return id;}
  public Long getPostId(){return postId;}
  public Long getReporterId(){return reporterId;}
  public String getReportStatus(){return reportStatus == null ? null : reportStatus.name();}
}
