package com.hsm.config;
import org.springframework.context.annotation.*; import org.springframework.web.servlet.config.annotation.*;
@Configuration public class WebConfig implements WebMvcConfigurer { @Override public void addCorsMappings(CorsRegistry r){r.addMapping("/api/**").allowedOrigins("${app.frontend-origin:http://localhost:5173}").allowedMethods("GET","POST","PUT","OPTIONS").allowedHeaders("*");} }
