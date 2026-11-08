package com.hsm.admin.entity;
import jakarta.persistence.*;
/** Read model mapped to the existing MySQL table; multi-table writes use native JPA queries within transactional services. */
@Entity
@org.hibernate.annotations.Immutable
@Table(name="complaints")
public class ComplaintRecord {
  public enum ComplaintStatus { PENDING, RESOLVED }
  @Id @Column(name="complaint_id") private Long id;
  @Column(name="order_id") private Long orderId;
  @Enumerated(EnumType.STRING)
  @Column(name="complaint_status", nullable=false) private ComplaintStatus complaintStatus;
  protected ComplaintRecord() {}
  public Long getId(){return id;}
  public Long getOrderId(){return orderId;}
  public String getComplaintStatus(){return complaintStatus == null ? null : complaintStatus.name();}
}
