package com.danyaell.mavericklabsbe.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Global CORS configuration class.
 * This configuration class applies CORS settings globally to all endpoints.
 * The actual values are loaded from application.yaml and can be overridden
 * by environment-specific profiles (e.g., application-local.yaml)
 * Benefits of this approach:
 * - No hardcoded URLs in code
 * - Easy to change per environment without recompilation
 * - Centralized CORS configuration
 * - Follows Spring Boot best practices
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final CorsProperties corsProperties;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(corsProperties.getAllowedOrigins())
                .allowedMethods(corsProperties.getAllowedMethods().split(","))
                .allowedHeaders((corsProperties.getAllowedHeaders().equals("*") ? "*" : corsProperties.getAllowedHeaders().split(",")).toString())
                .allowCredentials(corsProperties.getAllowCredentials())
                .maxAge(corsProperties.getMaxAge());
    }
}

