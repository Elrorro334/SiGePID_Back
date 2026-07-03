package com.sigepid.order.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * Cliente Feign declarativo para comunicarse con el microservicio de catálogo (catalog-service).
 *
 * @FeignClient(name = "catalog-service") - Define el nombre del servicio destino.
 * Spring Cloud lo resuelve automáticamente usando el Service Discovery (Eureka/Consul)
 * para obtener la URL real del servicio sin necesidad de hardcodearla.
 *
 * Feign genera la implementación HTTP automáticamente a partir de la interfaz,
 * convirtiendo las llamadas a métodos en peticiones HTTP reales.
 */
@FeignClient(name = "catalog-service")
public interface CatalogClient {

    /**
     * Reduce el stock de productos en el catálogo al crear un pedido.
     * Realiza una petición HTTP PUT al endpoint /api/catalog/products/reduce-stock del catalog-service.
     *
     * @param stockRequests Lista de mapas con los datos de reducción de stock.
     *                      Cada mapa contiene:
     *                      - "productId": ID del producto cuyo stock se debe reducir.
     *                      - "quantity": Cantidad de unidades a descontar del inventario.
     * @return Lista de mapas con la respuesta del servicio de catálogo (productos actualizados).
     */
    @PutMapping("/api/catalog/products/reduce-stock")
    List<Map<String, Object>> reduceStock(@RequestBody List<Map<String, Object>> stockRequests);
}
