package com.sigepid.notification.domain.entity;

import com.sigepid.notification.domain.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad de dominio que representa una notificación del sistema.
 * Actualmente se almacena en memoria (ConcurrentHashMap en el servicio).
 * Podría migrarse a una base de datos NoSQL (MongoDB) o relacional en el futuro.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    /** Identificador único de la notificación (UUID). */
    private String id;

    /** ID del usuario destinatario. */
    private String userId;

    /** Título breve de la notificación. */
    private String title;

    /** Contenido detallado del mensaje. */
    private String message;

    /** Categoría o tipo de la notificación. */
    private NotificationType type;

    /**
     * Indica si la notificación fue leída.
     * Se inicializa en false por defecto gracias a @Builder.Default.
     */
    @Builder.Default
    private Boolean read = false;

    /** Fecha y hora en que se creó la notificación. */
    private LocalDateTime createdAt;
}
