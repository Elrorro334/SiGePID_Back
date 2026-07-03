package com.sigepid.notification.domain.enums;

/**
 * Enum que define los tipos de notificación soportados por el sistema.
 * Cada valor representa una categoría distinta de evento que genera una notificación.
 */
public enum NotificationType {

    /** Se creó un nuevo pedido en el sistema. */
    ORDER_CREATED,

    /** El estado de un pedido existente fue actualizado. */
    ORDER_STATUS_CHANGED,

    /** Alerta de inventario bajo para un producto. */
    LOW_STOCK_ALERT,

    /** Alerta general del sistema (mantenimiento, errores, etc.). */
    SYSTEM_ALERT,

    /** Notificación de bienvenida para usuarios nuevos. */
    WELCOME
}
