package com.sigepid.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * CORS configuration for the API Gateway.
 * Uses a CorsWebFilter bean with highest priority to ensure CORS headers
 * are added BEFORE any other filter (JWT, routing, etc).
 */
@Configuration
public class GatewayConfig {

    @Value("${CORS_ALLOWED_ORIGIN:http://localhost:3000}")
    private String corsAllowedOrigin;

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Allowed origins
        config.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                corsAllowedOrigin
        ));

        // Allowed methods
        config.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));

        // Allowed headers
        config.setAllowedHeaders(List.of("*"));

        // Allow credentials (cookies, Authorization header)
        config.setAllowCredentials(true);

        // How long the browser should cache the preflight response (1 hour)
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }
}
