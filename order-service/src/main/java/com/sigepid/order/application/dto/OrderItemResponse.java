package com.sigepid.order.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO (Data Transfer Object) que representa la respuesta de un ítem individual
 * dentro de un pedido.
 *
 * Se utiliza para devolver la información de cada producto del pedido al cliente,
 * sin exponer directamente la entidad JPA (OrderItem).
 *
 * Anotaciones de Lombok:
 * - @Data: Genera getters, setters, toString, equals y hashCode.
 * - @Builder: Patrón Builder para construir instancias.
 * - @NoArgsConstructor / @AllArgsConstructor: Constructores vacío y completo.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {

    /** Identificador único del ítem. */
    private Long id;

    /** ID del producto en el catálogo. */
    private String productId;

    /** Nombre del producto. */
    private String productName;

    /** Cantidad de unidades solicitadas. */
    private Integer quantity;

    /** Precio unitario del producto al momento del pedido. */
    private BigDecimal unitPrice;

    /** Subtotal del ítem (unitPrice * quantity). */
    private BigDecimal subtotal;
}
