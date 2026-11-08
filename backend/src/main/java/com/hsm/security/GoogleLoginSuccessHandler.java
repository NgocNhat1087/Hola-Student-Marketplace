package com.hsm.security;

import com.hsm.account.entity.User;
import com.hsm.account.entity.UserProfile;
import com.hsm.account.service.AccountService;
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
import org.slf4j.Logger; import org.slf4j.LoggerFactory;

@Component
public class GoogleLoginSuccessHandler implements AuthenticationSuccessHandler {
  private static final Logger log=LoggerFactory.getLogger(GoogleLoginSuccessHandler.class); private final AccountService accounts;
  private final SessionService sessions; private final String frontendOrigin;
  public GoogleLoginSuccessHandler(AccountService accounts,
      SessionService sessions, @Value("${app.frontend-origin:http://localhost:5173}") String frontendOrigin) {
    this.accounts = accounts; this.sessions = sessions; this.frontendOrigin = frontendOrigin;
  }
  @Override public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {
    OAuth2User google = ((OAuth2AuthenticationToken) authentication).getPrincipal();
    String email = google.getAttribute("email"); String subject = google.getAttribute("sub");
    if (email == null || subject == null || email.isBlank() || subject.isBlank()) {
      response.sendRedirect(frontendOrigin + "/login?oauthError=" + encode("Google did not return a verified email address.")); return;
    }
    try { User user=accounts.loginWithGoogle(email,subject); response.sendRedirect(frontendOrigin + "/oauth2/callback?token=" + encode(sessions.create(user))); }
    catch (RuntimeException exception) { log.error("Google login failed while creating session for {}",email,exception); response.sendRedirect(frontendOrigin + "/login?oauthError=" + encode("Google account could not be linked. Please try again.")); }
  }
  private static String encode(String value) { return URLEncoder.encode(value, StandardCharsets.UTF_8); }
}
