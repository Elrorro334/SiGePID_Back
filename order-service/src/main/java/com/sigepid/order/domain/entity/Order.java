package com.sigepid.order.domain.entity;

import com.sigepid.order.domain.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad JPA que representa un pedido en el sistema.
 * Se mapea a la tabla "orders" en la base de datos.
 *
 * Anotaciones de Lombok:
 * - @Data: Genera getters, setters, toString, equals y hashCode automáticamente.
 * - @Builder: Permite crear instancias usando el patrón Builder (Order.builder()...build()).
 * - @NoArgsConstructor / @AllArgsConstructor: Genera constructores vacío y completo.
 */
@Entity
@Table(name = "orders") // Se usa "orders" porque "order" es palabra reservada en SQL
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    /** Identificador único del pedido, generado automáticamente por la base de datos (autoincremental). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** ID del usuario que realizó el pedido. Es obligatorio y se mapea a la columna "user_id". */
    @Column(name = "user_id", nullable = false)
    private String userId;

    /**
     * Estado actual del pedido (PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED).
     * Se almacena como texto en la base de datos gracias a EnumType.STRING.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    /**
     * Monto total del pedido, calculado como la suma de los subtotales de todos los ítems.
     * Se usa BigDecimal para precisión monetaria (19 dígitos, 2 decimales).
     */
    @Column(name = "total_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount;

    /** Dirección de envío proporcionada por el usuario al crear el pedido. */
    @Column(name = "shipping_address", nullable = false)
    private String shippingAddress;

    /** Fecha y hora en que se creó el pedido. No se puede actualizar una vez establecida. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Fecha y hora de la última actualización del pedido. */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Lista de ítems (productos) que componen este pedido.
     * - mappedBy = "order": La relación es gestionada por el campo "order" en OrderItem.
     * - CascadeType.ALL: Cualquier operación (persist, merge, remove) se propaga a los ítems.
     * - orphanRemoval = true: Si se elimina un ítem de la lista, también se elimina de la BD.
     * - @Builder.Default: Inicializa la lista vacía al usar el patrón Builder.
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    /**
     * Callback de JPA que se ejecuta automáticamente ANTES de insertar el pedido en la BD.
     * Establece las fechas de creación y actualización al momento actual.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Callback de JPA que se ejecuta automáticamente ANTES de actualizar el pedido en la BD.
     * Actualiza la fecha de última modificación al momento actual.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
