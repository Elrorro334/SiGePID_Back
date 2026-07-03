package com.sigepid.order.application.dto;

import com.sigepid.order.domain.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO (Data Transfer Object) que representa la respuesta devuelta al cliente
 * con la información de un pedido.
 *
 * Se utiliza para enviar datos al frontend o API externa sin exponer directamente
 * la entidad JPA (Order), manteniendo así la separación de capas.
 *
 * Anotaciones de Lombok:
 * - @Data: Genera getters, setters, toString, equals y hashCode.
 * - @Builder: Patrón Builder para construir instancias de forma fluida.
 * - @NoArgsConstructor / @AllArgsConstructor: Constructores vacío y completo.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    /** Identificador único del pedido. */
    private Long id;

    /** ID del usuario que realizó el pedido. */
    private String userId;

    /** Estado actual del pedido (PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED). */
    private OrderStatus status;

    /** Monto total del pedido (suma de todos los subtotales de los ítems). */
    private BigDecimal totalAmount;

    /** Dirección de envío del pedido. */
    private String shippingAddress;

    /** Lista de ítems (productos) que componen el pedido, mapeados como OrderItemResponse. */
    private List<OrderItemResponse> items;

    /** Fecha y hora en que se creó el pedido. */
    private LocalDateTime createdAt;

    /** Fecha y hora de la última actualización del pedido. */
    private LocalDateTime updatedAt;
}
