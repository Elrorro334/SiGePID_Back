package com.sigepid.order.presentation.controller;

import com.sigepid.order.application.dto.OrderRequest;
import com.sigepid.order.application.dto.OrderResponse;
import com.sigepid.order.application.service.OrderService;
import com.sigepid.order.domain.enums.OrderStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST que expone los endpoints HTTP para la gestión de pedidos.
 * Es el punto de entrada de las peticiones HTTP del cliente hacia la lógica de negocio.
 *
 * @RestController      - Combina @Controller y @ResponseBody: cada método retorna datos JSON directamente.
 * @RequestMapping      - Define la ruta base "/api/orders" para todos los endpoints de este controlador.
 * @RequiredArgsConstructor - Genera constructor con los campos finales para inyección de dependencias.
 *
 * Endpoints disponibles:
 * - POST   /api/orders              → Crear un nuevo pedido
 * - GET    /api/orders/{id}         → Obtener un pedido por su ID
 * - GET    /api/orders/user/{userId} → Obtener todos los pedidos de un usuario
 * - PUT    /api/orders/{id}/status  → Actualizar el estado de un pedido
 * - PUT    /api/orders/{id}/cancel  → Cancelar un pedido
 * - GET    /api/orders/status/{status} → Obtener pedidos por estado
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    /** Servicio de pedidos que contiene la lógica de negocio. Se inyecta automáticamente por Spring. */
    private final OrderService orderService;

    /**
     * Endpoint para crear un nuevo pedido.
     * Método HTTP: POST /api/orders
     *
     * @param request Cuerpo de la petición con los datos del pedido.
     *                @Valid activa la validación automática de los campos (NotBlank, NotEmpty, etc.).
     * @return ResponseEntity con el pedido creado y código HTTP 201 (CREATED).
     */
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Endpoint para obtener un pedido por su ID.
     * Método HTTP: GET /api/orders/{id}
     *
     * @param id ID del pedido, extraído de la URL.
     * @return ResponseEntity con el pedido encontrado y código HTTP 200 (OK).
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        OrderResponse response = orderService.getOrderById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para obtener todos los pedidos de un usuario específico.
     * Método HTTP: GET /api/orders/user/{userId}
     *
     * @param userId ID del usuario, extraído de la URL.
     * @return ResponseEntity con la lista de pedidos del usuario y código HTTP 200 (OK).
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByUserId(@PathVariable String userId) {
        List<OrderResponse> responses = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(responses);
    }

    /**
     * Endpoint para actualizar el estado de un pedido existente.
     * Método HTTP: PUT /api/orders/{id}/status?status=CONFIRMED
     *
     * @param id     ID del pedido, extraído de la URL.
     * @param status Nuevo estado del pedido, recibido como parámetro de query (?status=CONFIRMED).
     * @return ResponseEntity con el pedido actualizado y código HTTP 200 (OK).
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {
        OrderResponse response = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para cancelar un pedido.
     * Método HTTP: PUT /api/orders/{id}/cancel
     * No se puede cancelar un pedido que ya fue entregado (DELIVERED).
     *
     * @param id ID del pedido a cancelar, extraído de la URL.
     * @return ResponseEntity vacío con código HTTP 204 (NO CONTENT) si se canceló exitosamente.
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint para obtener todos los pedidos filtrados por estado.
     * Método HTTP: GET /api/orders/status/{status}
     *
     * @param status Estado del pedido para filtrar (PENDING, CONFIRMED, PROCESSING, etc.).
     * @return ResponseEntity con la lista de pedidos filtrados y código HTTP 200 (OK).
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderResponse>> getOrdersByStatus(@PathVariable OrderStatus status) {
        List<OrderResponse> responses = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(responses);
    }
}
