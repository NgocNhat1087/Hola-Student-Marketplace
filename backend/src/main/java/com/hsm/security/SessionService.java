package com.hsm.security;

import com.hsm.account.entity.User;
import com.hsm.account.repository.UserRepository;
import com.hsm.common.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionService {
  private static final Duration SESSION_TTL = Duration.ofHours(1);
  private final UserRepository users;
  private final byte[] secret;
  private final Map<String, Long> revokedTokens = new ConcurrentHashMap<>();

  public SessionService(UserRepository users,
      @Value("${app.session-secret:dev-only-change-this-session-secret}") String sessionSecret) {
    this.users = users;
    this.secret = sessionSecret.getBytes(StandardCharsets.UTF_8);
  }

  public String create(User user) {
    long expiresAt = Instant.now().plus(SESSION_TTL).getEpochSecond();
    String payload = user.getId() + ":" + expiresAt;
    String encodedPayload = encode(payload.getBytes(StandardCharsets.UTF_8));
    return encodedPayload + "." + encode(sign(encodedPayload));
  }

  public User require(String token) {
    if (token == null || token.isBlank()) throw unauthenticated();
    long now = Instant.now().getEpochSecond();
    revokedTokens.entrySet().removeIf(entry -> entry.getValue() <= now);
    if (revokedTokens.containsKey(token)) throw unauthenticated();
    String[] parts = token.split("\\.", -1);
    if (parts.length != 2 || !MessageDigest.isEqual(sign(parts[0]), decode(parts[1]))) {
      throw unauthenticated();
    }
    String[] values = new String(decode(parts[0]), StandardCharsets.UTF_8).split(":", -1);
    if (values.length != 2) throw unauthenticated();
    try {
      long userId = Long.parseLong(values[0]);
      long expiresAt = Long.parseLong(values[1]);
      if (now >= expiresAt) throw unauthenticated();
      return users.findById(userId).orElseThrow(SessionService::unauthenticated);
    } catch (NumberFormatException exception) {
      throw unauthenticated();
    }
  }

  public void remove(String token) {
    if (token == null || token.isBlank()) return;
    revokedTokens.put(token, Instant.now().plus(SESSION_TTL).getEpochSecond());
  }

  private byte[] sign(String value) {
    try {
      Mac mac = Mac.getInstance("HmacSHA256");
      mac.init(new SecretKeySpec(secret, "HmacSHA256"));
      return mac.doFinal(value.getBytes(StandardCharsets.UTF_8));
    } catch (Exception exception) {
      throw new IllegalStateException("Could not sign session token", exception);
    }
  }

  private static String encode(byte[] value) {
    return Base64.getUrlEncoder().withoutPadding().encodeToString(value);
  }

  private static byte[] decode(String value) {
    try {
      return Base64.getUrlDecoder().decode(value);
    } catch (IllegalArgumentException exception) {
      throw unauthenticated();
    }
  }

  private static ApiException unauthenticated() {
    return new ApiException(401, "UNAUTHENTICATED", "Authentication is required.");
  }

}
