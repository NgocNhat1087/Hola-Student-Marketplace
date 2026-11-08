package com.hsm.account.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity @Table(name="users", indexes={@Index(name="idx_users_email", columnList="email", unique=true)})
public class User {
  public enum Role { USER, ADMIN }
  public enum Status { ACTIVE, LOCKED }
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) @Column(name="user_id") private Long id;
  @Column(nullable=false, unique=true, length=255) private String email;
  @Column(name="password_hash", length=255) private String passwordHash;
  @Column(name="google_subject", unique=true) private String googleSubject;
  @Enumerated(EnumType.STRING) @Column(name="account_role", nullable=false) private Role role=Role.USER;
  @Enumerated(EnumType.STRING) @Column(name="account_status", nullable=false) private Status status=Status.ACTIVE;
  private Instant emailVerifiedAt; @Column(name="otp_code") private String otpHash; private Instant otpExpiresAt; private byte otpFailedCount; private Instant otpSentAt;
  private byte failedLoginCount; private Instant loginLockedUntil; private Instant lastLoginAt; @Column(nullable=false) private Instant createdAt=Instant.now();
  public Long getId(){return id;} public String getEmail(){return email;} public void setEmail(String v){email=v;} public String getPasswordHash(){return passwordHash;} public void setPasswordHash(String v){passwordHash=v;} public String getGoogleSubject(){return googleSubject;} public void setGoogleSubject(String v){googleSubject=v;} public Role getRole(){return role;} public Status getStatus(){return status;} public Instant getEmailVerifiedAt(){return emailVerifiedAt;} public void setEmailVerifiedAt(Instant v){emailVerifiedAt=v;} public String getOtpHash(){return otpHash;} public void setOtpHash(String v){otpHash=v;} public Instant getOtpExpiresAt(){return otpExpiresAt;} public void setOtpExpiresAt(Instant v){otpExpiresAt=v;} public byte getOtpFailedCount(){return otpFailedCount;} public void setOtpFailedCount(int v){otpFailedCount=(byte)v;} public Instant getOtpSentAt(){return otpSentAt;} public void setOtpSentAt(Instant v){otpSentAt=v;} public byte getFailedLoginCount(){return failedLoginCount;} public void setFailedLoginCount(int v){failedLoginCount=(byte)v;} public Instant getLoginLockedUntil(){return loginLockedUntil;} public void setLoginLockedUntil(Instant v){loginLockedUntil=v;} public void setLastLoginAt(Instant v){lastLoginAt=v;}
}
