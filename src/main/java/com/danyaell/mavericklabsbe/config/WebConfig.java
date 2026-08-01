package com.danyaell.mavericklabsbe.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final CorsProperties corsProperties;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        corsProperties.getAllowedOrigins().toArray(String[]::new)
                )
                .allowedMethods(
                        corsProperties.getAllowedMethods().toArray(String[]::new)
                )
                .allowedHeaders(
                        corsProperties.getAllowedHeaders().toArray(String[]::new)
                )
                .allowCredentials(corsProperties.isAllowCredentials())
                .maxAge(corsProperties.getMaxAge());
    }
}