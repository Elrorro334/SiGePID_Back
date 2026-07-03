package com.sigepid.order.domain.enums;

/**
 * Enumeración que define los posibles estados por los que pasa un pedido
 * dentro de su ciclo de vida en el sistema.
 * Se almacena como cadena de texto (String) en la base de datos gracias a @Enumerated(EnumType.STRING).
 */
public enum OrderStatus {
    PENDING,     // Pedido creado, pendiente de confirmación
    CONFIRMED,   // Pedido confirmado por el sistema o el administrador
    PROCESSING,  // Pedido en proceso de preparación
    SHIPPED,     // Pedido enviado al cliente
    DELIVERED,   // Pedido entregado exitosamente al cliente
    CANCELLED    // Pedido cancelado (no puede cancelarse si ya fue entregado)
}
