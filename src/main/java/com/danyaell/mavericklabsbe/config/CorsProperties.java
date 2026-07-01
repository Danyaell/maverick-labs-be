package com.danyaell.mavericklabsbe.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * CORS configuration properties loaded from application.yaml
 * This allows different CORS settings per environment (local, dev, prod)
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.cors")
public class CorsProperties {
    private String allowedOrigins;
    private String allowedMethods;
    private String allowedHeaders;
    private Boolean allowCredentials;
    private Long maxAge;
}

