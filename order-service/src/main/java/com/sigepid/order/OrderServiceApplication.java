package com.sigepid.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Clase principal del microservicio de gestión de pedidos (Order Service).
 *
 * @SpringBootApplication - Habilita la autoconfiguración de Spring Boot, el escaneo de componentes
 *                          y la configuración automática de beans dentro del paquete base.
 * @EnableFeignClients    - Activa los clientes Feign declarativos, permitiendo que este microservicio
 *                          se comunique con otros servicios (como catalog-service) mediante interfaces HTTP.
 */
@SpringBootApplication
@EnableFeignClients
public class OrderServiceApplication {

    /**
     * Método de entrada de la aplicación.
     * Arranca el contexto de Spring Boot y levanta el servidor embebido (Tomcat por defecto).
     *
     * @param args argumentos de línea de comandos pasados al iniciar la aplicación.
     */
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
