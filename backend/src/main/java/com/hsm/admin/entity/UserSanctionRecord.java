package com.hsm.admin.entity;
import jakarta.persistence.*;
/** Read model mapped to the existing MySQL table; multi-table writes use native JPA queries within transactional services. */
@Entity
@org.hibernate.annotations.Immutable
@Table(name="user_sanctions")
public class UserSanctionRecord {
  public enum SanctionType { WARNING, LOCK }
  @Id @Column(name="sanction_id") private Long id;
  @Column(name="user_id") private Long userId;
  @Enumerated(EnumType.STRING)
  @Column(name="sanction_type", nullable=false) private SanctionType sanctionType;
  protected UserSanctionRecord() {}
  public Long getId(){return id;}
  public Long getUserId(){return userId;}
  public String getSanctionType(){return sanctionType == null ? null : sanctionType.name();}
}
