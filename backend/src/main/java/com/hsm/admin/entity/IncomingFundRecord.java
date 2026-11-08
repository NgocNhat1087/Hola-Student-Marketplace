package com.hsm.admin.entity;
import jakarta.persistence.*;
/** Read model mapped to the existing MySQL table; multi-table writes use native JPA queries within transactional services. */
@Entity
@org.hibernate.annotations.Immutable
@Table(name="incoming_funds")
public class IncomingFundRecord {
  public enum FundStatus { UNIDENTIFIED, MATCHED, RECONCILED }
  @Id @Column(name="fund_id") private Long id;
  @Column(name="boost_transaction_id") private Long boostTransactionId;
  @Enumerated(EnumType.STRING)
  @Column(name="fund_status", nullable=false) private FundStatus fundStatus;
  protected IncomingFundRecord() {}
  public Long getId(){return id;}
  public Long getBoostTransactionId(){return boostTransactionId;}
  public String getFundStatus(){return fundStatus == null ? null : fundStatus.name();}
}
