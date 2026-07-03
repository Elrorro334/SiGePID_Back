package com.sigepid.notification.application.dto;

import com.sigepid.notification.domain.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta que representa una notificación devuelta por la API.
 * Se utiliza para exponer los datos de la notificación al cliente
 * sin acoplar la capa de presentación con la entidad de dominio.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    /** Identificador único de la notificación (UUID generado). */
    private String id;

    /** ID del usuario al que pertenece la notificación. */
    private String userId;

    /** Título de la notificación. */
    private String title;

    /** Cuerpo o contenido del mensaje. */
    private String message;

    /** Tipo de notificación según el enum NotificationType. */
    private NotificationType type;

    /** Indica si la notificación ha sido leída por el usuario. */
    private Boolean read;

    /** Fecha y hora de creación de la notificación. */
    private LocalDateTime createdAt;
}
