package com.sigepid.notification.application.dto;

import com.sigepid.notification.domain.enums.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) que representa la solicitud para crear una notificación.
 * Contiene validaciones de Jakarta Bean Validation para asegurar datos obligatorios.
 * Lombok genera automáticamente getters, setters, constructor, builder y toString.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {

    /** Identificador del usuario destinatario de la notificación. No puede estar vacío. */
    @NotBlank(message = "User ID is required")
    private String userId;

    private String userEmail;

    @NotBlank(message = "Title is required")
    private String title;

    /** Contenido o cuerpo del mensaje de la notificación. No puede estar vacío. */
    @NotBlank(message = "Message is required")
    private String message;

    /** Tipo de notificación (ej. ORDER_CREATED, LOW_STOCK_ALERT). No puede ser nulo. */
    @NotNull(message = "Notification type is required")
    private NotificationType type;
}
