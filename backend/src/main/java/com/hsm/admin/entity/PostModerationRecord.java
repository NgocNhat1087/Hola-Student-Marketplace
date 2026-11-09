package com.hsm.admin.entity;
import jakarta.persistence.*;
/** Read model mapped to the existing MySQL table; multi-table writes use native JPA queries within transactional services. */
@Entity
@org.hibernate.annotations.Immutable
@Table(name="posts")
public class PostModerationRecord {
  public enum ApprovalStatus { PENDING, APPROVED, REJECTED }
  @Id @Column(name="post_id") private Long id;
  @Column(name="seller_id") private Long sellerId;
  @Enumerated(EnumType.STRING)
  @Column(name="approval_status", nullable=false) private ApprovalStatus approvalStatus;
  @Column(name="title", nullable=false, length=200) private String title;
  protected PostModerationRecord() {}
  public Long getId(){return id;}
  public Long getSellerId(){return sellerId;}
  public String getApprovalStatus(){return approvalStatus == null ? null : approvalStatus.name();}
  public String getTitle(){return title;}
}
