package com.hsm.config;

import com.hsm.security.GoogleLoginFailureHandler;
import com.hsm.security.GoogleLoginSuccessHandler;
import com.hsm.security.SessionFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
  @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
  @Bean SecurityFilterChain security(HttpSecurity http, SessionFilter sessionFilter,
      GoogleLoginSuccessHandler googleSuccess, GoogleLoginFailureHandler googleFailure,
      @Value("${spring.security.oauth2.client.registration.google.client-id:}") String googleClientId) throws Exception {
    var base = http.csrf(c -> c.disable()).cors(c -> {})
        .authorizeHttpRequests(a -> a.requestMatchers("/api/auth/**", "/health", "/error", "/oauth2/**", "/login/**").permitAll()
            .anyRequest().permitAll())
        .addFilterBefore(sessionFilter, UsernamePasswordAuthenticationFilter.class);
    if (!googleClientId.isBlank() && !googleClientId.equalsIgnoreCase("change-me")) {
      base.oauth2Login(o -> o
          // The custom handler creates the application session and redirects to the frontend callback.
          .successHandler(googleSuccess)
          .failureHandler(googleFailure));
    }
    return base.build();
  }
}
