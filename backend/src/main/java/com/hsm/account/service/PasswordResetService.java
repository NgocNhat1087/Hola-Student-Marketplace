package com.hsm.account.service;

import com.hsm.account.dto.AuthDtos;
import com.hsm.account.entity.User;
import com.hsm.account.repository.UserRepository;
import com.hsm.common.ApiException;
import com.hsm.integration.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Service
public class PasswordResetService {
  private static final long RESET_TTL_SECONDS = 15 * 60;
  private final UserRepository users;
  private final PasswordEncoder encoder;
  private final EmailService email;
  private final String frontendOrigin;
  private final SecureRandom random = new SecureRandom();

  public PasswordResetService(UserRepository users, PasswordEncoder encoder, EmailService email,
      @Value("${app.frontend-origin:http://localhost:5173}") String frontendOrigin) {
    this.users = users;
    this.encoder = encoder;
    this.email = email;
    this.frontendOrigin = frontendOrigin;
  }

  @Transactional
  public void request(AuthDtos.EmailRequest request) {
    users.findByEmailIgnoreCase(request.email().trim().toLowerCase()).ifPresent(user -> {
      String rawToken = createToken();
      user.setResetToken(hash(rawToken));
      user.setResetTokenExpiresAt(Instant.now().plusSeconds(RESET_TTL_SECONDS));
      users.save(user);
      email.sendPasswordResetLink(user.getEmail(), frontendOrigin + "/reset-password?token=" + rawToken);
    });
  }

  @Transactional
  public void reset(AuthDtos.ResetPasswordRequest request) {
    User user = users.findByResetToken(hash(request.token())).orElseThrow(PasswordResetService::invalidToken);
    if (user.getResetTokenExpiresAt() == null || user.getResetTokenExpiresAt().isBefore(Instant.now())) {
      throw invalidToken();
    }
    user.setPasswordHash(encoder.encode(request.password()));
    user.setResetToken(null);
    user.setResetTokenExpiresAt(null);
    users.save(user);
  }

  private String createToken() {
    byte[] bytes = new byte[32];
    random.nextBytes(bytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
  }

  private static String hash(String token) {
    try {
      byte[] digest = MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8));
      return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
    } catch (Exception exception) {
      throw new IllegalStateException("Could not hash password reset token", exception);
    }
  }

  private static ApiException invalidToken() {
    return new ApiException(400, "RESET_TOKEN_INVALID", "This password reset link is invalid or expired. Request a new link.");
  }
}
