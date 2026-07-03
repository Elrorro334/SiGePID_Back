package com.sigepid.order.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO (Data Transfer Object) que representa la solicitud de un ítem individual
 * dentro de un pedido.
 *
 * Contiene la información de un producto que el usuario desea agregar al pedido.
 * Se valida automáticamente gracias a Jakarta Bean Validation cuando se recibe
 * en el controlador (activado por @Valid en OrderRequest).
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
public class OrderItemRequest {

    /** ID del producto en el catálogo. No puede estar vacío. */
    @NotBlank(message = "Product ID is required")
    private String productId;

    /** Nombre del producto. No puede estar vacío. */
    @NotBlank(message = "Product name is required")
    private String productName;

    /**
     * Cantidad de unidades solicitadas del producto.
     * - @NotNull: No puede ser nulo.
     * - @Min(1): Debe ser al menos 1 unidad.
     */
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    /**
     * Precio unitario del producto.
     * - @NotNull: No puede ser nulo.
     * - @Positive: Debe ser un valor positivo (mayor que 0).
     */
    @NotNull(message = "Unit price is required")
    @Positive(message = "Unit price must be positive")
    private BigDecimal unitPrice;
}
