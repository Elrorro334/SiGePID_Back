package com.sigepid.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Clase principal del microservicio de notificaciones.
 * Utiliza @SpringBootApplication para habilitar la auto-configuración,
 * el escaneo de componentes y la definición de beans dentro de este paquete.
 */
@SpringBootApplication
@EnableFeignClients
public class NotificationServiceApplication {

    /**
     * Punto de entrada de la aplicación.
     * Inicializa el contexto de Spring Boot y levanta el servidor embebido.
     *
     * @param args argumentos de línea de comandos
     */
    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }
}
