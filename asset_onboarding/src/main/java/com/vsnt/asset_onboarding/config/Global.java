package com.vsnt.asset_onboarding.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class Global implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/*")
                .allowedOrigins("http://localhost:5173") // your frontend
                .allowedMethods("*")
                .allowedHeaders("Authorization,Content-Type") // allow Authorization header
                .exposedHeaders("*");

    }
}
