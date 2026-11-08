package com.hsm.integration;
public interface EmailService { void sendVerificationCode(String email,String otp); void sendPasswordResetLink(String email,String link); }
