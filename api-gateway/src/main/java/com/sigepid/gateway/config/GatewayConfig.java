package com.sigepid.gateway.config;

import org.springframework.context.annotation.Configuration;

/**
 * Custom Gateway configuration.
 * <p>
 * Routes are primarily defined in application.yml.
 * Use this class for programmatic route definitions or
 * custom filter/predicate factories if needed.
 * </p>
 */
@Configuration
public class GatewayConfig {

    // Programmatic route definitions can be added here using RouteLocatorBuilder
    // Example:
    // @Bean
    // public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
    //     return builder.routes()
    //             .route("custom-route", r -> r.path("/custom/**")
    //                     .uri("lb://custom-service"))
    //             .build();
    // }

}
