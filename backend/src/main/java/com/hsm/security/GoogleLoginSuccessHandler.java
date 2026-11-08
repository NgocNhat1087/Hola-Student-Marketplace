package com.hsm.security;

import com.hsm.account.entity.User;
import com.hsm.account.entity.UserProfile;
import com.hsm.account.repository.UserProfileRepository;
import com.hsm.account.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Component
public class GoogleLoginSuccessHandler implements AuthenticationSuccessHandler {
  private final UserRepository users; private final UserProfileRepository profiles;
  private final SessionService sessions; private final String frontendOrigin;
  public GoogleLoginSuccessHandler(UserRepository users, UserProfileRepository profiles,
      SessionService sessions, @Value("${app.frontend-origin:http://localhost:5173}") String frontendOrigin) {
    this.users = users; this.profiles = profiles; this.sessions = sessions; this.frontendOrigin = frontendOrigin;
  }
  @Override public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {
    OAuth2User google = ((OAuth2AuthenticationToken) authentication).getPrincipal();
    String email = google.getAttribute("email"); String subject = google.getAttribute("sub");
    if (email == null || subject == null || email.isBlank() || subject.isBlank()) {
      response.sendRedirect(frontendOrigin + "/login?oauthError=" + encode("Google did not return a verified email address.")); return;
    }
    User user = users.findByGoogleSubject(subject).orElseGet(() -> users.findByEmailIgnoreCase(email).orElseGet(() -> {
      User created = new User(); created.setEmail(email.toLowerCase()); created.setGoogleSubject(subject); created.setEmailVerifiedAt(Instant.now());
      User saved = users.save(created); String localPart = email.substring(0, email.indexOf('@'));
      profiles.save(new UserProfile(saved, localPart.substring(0, Math.min(80, localPart.length())))); return saved;
    }));
    if (user.getGoogleSubject() == null) { user.setGoogleSubject(subject); user.setEmailVerifiedAt(Instant.now()); users.save(user); }
    response.sendRedirect(frontendOrigin + "/oauth2/callback#token=" + encode(sessions.create(user)));
  }
  private static String encode(String value) { return URLEncoder.encode(value, StandardCharsets.UTF_8); }
}
