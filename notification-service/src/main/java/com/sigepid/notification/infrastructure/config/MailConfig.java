package com.sigepid.notification.infrastructure.config;

import org.springframework.context.annotation.Configuration;

/**
 * Clase de configuración para el servicio de correo electrónico.
 * Actualmente sirve como placeholder; la configuración SMTP se toma
 * de application.yml (spring.mail.*).
 *
 * Aquí se pueden definir beans personalizados como:
 * - JavaMailSender con configuración avanzada
 * - Motores de plantillas (Thymeleaf, FreeMarker) para correos HTML
 * - Pools de conexiones SMTP
 */
@Configuration
public class MailConfig {

    // Reservado para configuración futura del servicio de correo.
    // Se pueden agregar beans de JavaMailSender, motores de plantillas, etc.
}
