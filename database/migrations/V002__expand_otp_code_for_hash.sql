-- The application stores the OTP as a BCrypt hash, not as the six-digit value.
ALTER TABLE users MODIFY COLUMN otp_code VARCHAR(255) NULL;
