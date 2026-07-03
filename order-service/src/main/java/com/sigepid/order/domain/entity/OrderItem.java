package com.sigepid.order.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Entidad JPA que representa un ítem (producto) dentro de un pedido.
 * Se mapea a la tabla "order_items" en la base de datos.
 * Cada OrderItem pertenece a exactamente un Order (relación ManyToOne).
 *
 * Anotaciones de Lombok:
 * - @Data: Genera getters, setters, toString, equals y hashCode.
 * - @Builder: Patrón Builder para construir instancias de forma fluida.
 * - @NoArgsConstructor / @AllArgsConstructor: Constructores vacío y completo.
 */
@Entity
@Table(name = "order_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    /** Identificador único del ítem, generado automáticamente (autoincremental). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** ID del producto en el catálogo (catalog-service). Obligatorio. */
    @Column(name = "product_id", nullable = false)
    private String productId;

    /** Nombre del producto al momento de crear el pedido. Se almacena para mantener historial. */
    @Column(name = "product_name", nullable = false)
    private String productName;

    /** Cantidad de unidades solicitadas de este producto. */
    @Column(nullable = false)
    private Integer quantity;

    /** Precio unitario del producto al momento del pedido. Precisión: 19 dígitos, 2 decimales. */
    @Column(name = "unit_price", nullable = false, precision = 19, scale = 2)
    private BigDecimal unitPrice;

    /** Subtotal del ítem (unitPrice * quantity). Se calcula al crear el pedido. */
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal subtotal;

    /**
     * Relación ManyToOne con la entidad Order (un pedido tiene muchos ítems).
     * - FetchType.LAZY: El pedido padre solo se carga cuando se accede explícitamente (mejora rendimiento).
     * - @JoinColumn: Clave foránea "order_id" en la tabla order_items.
     * - @JsonIgnore: Evita la serialización circular (Order -> Items -> Order -> Items...) al generar JSON.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    private Order order;
}
