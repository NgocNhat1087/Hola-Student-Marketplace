package com.hsm.config;
import org.springframework.beans.factory.annotation.Value; import org.springframework.context.annotation.*; import org.springframework.web.servlet.config.annotation.*;
@Configuration public class WebConfig implements WebMvcConfigurer { @Value("${app.frontend-origin:http://localhost:5173}") private String frontendOrigin; @Override public void addCorsMappings(CorsRegistry r){r.addMapping("/api/**").allowedOrigins(frontendOrigin,"http://127.0.0.1:5173").allowedMethods("GET","POST","PUT","OPTIONS").allowedHeaders("*").allowCredentials(false);} }
