package com.sigepid.order.infrastructure.config;

import org.springframework.context.annotation.Configuration;

/**
 * Clase de configuración para Resilience4j.
 *
 * @Configuration - Indica a Spring que esta clase puede contener definiciones de beans (@Bean).
 *
 * Resilience4j es una librería de tolerancia a fallos que proporciona patrones como:
 * - Circuit Breaker: Corta las llamadas a servicios que están fallando para evitar cascadas de errores.
 * - Retry: Reintenta automáticamente las llamadas fallidas.
 * - Rate Limiter: Limita la cantidad de llamadas por segundo.
 *
 * Actualmente, la configuración principal del Circuit Breaker se define en el archivo application.yml.
 * Si se necesitan beans personalizados de Resilience4j, se pueden definir aquí.
 */
@Configuration
public class ResilienceConfig {

    // Los beans personalizados de Resilience4j se pueden definir aquí.
    // La configuración principal del circuit breaker está en application.yml.
}
