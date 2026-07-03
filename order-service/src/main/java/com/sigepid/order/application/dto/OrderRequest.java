package com.sigepid.order.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO (Data Transfer Object) que representa la solicitud para crear un nuevo pedido.
 * Recibe los datos enviados por el cliente (frontend o API externa) en el cuerpo de la petición HTTP POST.
 *
 * Se utiliza validación de Jakarta Bean Validation para asegurar que los datos sean correctos
 * antes de procesarlos en la capa de servicio.
 *
 * Anotaciones de Lombok:
 * - @Data: Genera getters, setters, toString, equals y hashCode.
 * - @Builder: Permite construir instancias con el patrón Builder.
 * - @NoArgsConstructor / @AllArgsConstructor: Constructores vacío y completo.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {

    /** ID del usuario que realiza el pedido. No puede estar vacío ni ser solo espacios. */
    @NotBlank(message = "User ID is required")
    private String userId;

    private String userEmail;

    @NotBlank(message = "Shipping address is required")
    private String shippingAddress;

    /**
     * Lista de ítems (productos) que componen el pedido.
     * - @NotEmpty: La lista debe contener al menos un ítem.
     * - @Valid: Activa la validación en cascada, es decir, también valida cada OrderItemRequest internamente.
     */
    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    private List<OrderItemRequest> items;
}
