package com.hsm.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class GoogleLoginFailureHandler implements AuthenticationFailureHandler {
  private static final Logger log = LoggerFactory.getLogger(GoogleLoginFailureHandler.class);
  private final String frontendOrigin;
  public GoogleLoginFailureHandler(@Value("${app.frontend-origin:http://localhost:5173}") String frontendOrigin) { this.frontendOrigin = frontendOrigin; }
  @Override public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException exception) throws IOException, ServletException {
    log.error("Google OAuth authentication failed: {}", exception.getMessage(), exception);
    String message = "Google sign-in could not be completed. Check the OAuth client and redirect URI.";
    response.sendRedirect(frontendOrigin + "/login?oauthError=" + URLEncoder.encode(message, StandardCharsets.UTF_8));
  }
}
