package com.hsm.admin.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
/** UC39: JPA entity mapped to the existing receiving_bank_accounts table. */
@Entity
@Table(name="receiving_bank_accounts")
public class ReceivingBankAccountRecord {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
  @Column(name="bank_account_id") private Long id;
  @Column(name="bank_name",nullable=false,length=100) private String bankName;
  @Column(name="account_number",nullable=false,length=50) private String accountNumber;
  @Column(name="account_holder",nullable=false,length=150) private String accountHolder;
  @Column(name="is_enabled",nullable=false) private boolean enabled=false;
  @Column(name="created_by",nullable=false) private Long createdBy;
  @Column(name="created_at",insertable=false,updatable=false) private LocalDateTime createdAt;
  @Column(name="updated_at",insertable=false,updatable=false) private LocalDateTime updatedAt;
  protected ReceivingBankAccountRecord() {}
  public ReceivingBankAccountRecord(String bankName,String accountNumber,String accountHolder,Long adminId){
    this.bankName=bankName;this.accountNumber=accountNumber;this.accountHolder=accountHolder;this.createdBy=adminId;
  }
  public Long getId(){return id;}
  public String getBankName(){return bankName;}
  public String getAccountNumber(){return accountNumber;}
  public String getAccountHolder(){return accountHolder;}
  public boolean getEnabled(){return enabled;}
  public Long getCreatedBy(){return createdBy;}
  public LocalDateTime getCreatedAt(){return createdAt;}
  public void updateDetails(String bankName,String accountNumber,String accountHolder){
    this.bankName=bankName;this.accountNumber=accountNumber;this.accountHolder=accountHolder;
  }
  public void setEnabled(boolean enabled){this.enabled=enabled;}
}
