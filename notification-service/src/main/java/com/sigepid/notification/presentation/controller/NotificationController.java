package com.sigepid.notification.presentation.controller;

import com.sigepid.notification.application.dto.NotificationRequest;
import com.sigepid.notification.application.dto.NotificationResponse;
import com.sigepid.notification.application.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST para la gestión de notificaciones.
 * Expone los endpoints bajo la ruta base "/api/notifications".
 *
 * Endpoints disponibles:
 * - POST /api/notifications          → Crear una nueva notificación
 * - GET  /api/notifications/user/{id} → Obtener notificaciones por usuario
 * - PUT  /api/notifications/{id}/read → Marcar una notificación como leída
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    /** Servicio inyectado por constructor (gracias a @RequiredArgsConstructor de Lombok). */
    private final NotificationService notificationService;

    /**
     * Crea y envía una nueva notificación.
     * Valida el cuerpo de la solicitud con las anotaciones de Bean Validation.
     *
     * @param request datos de la notificación a crear (validados con @Valid)
     * @return ResponseEntity con la notificación creada y código HTTP 201 (CREATED)
     */
    @PostMapping
    public ResponseEntity<NotificationResponse> sendNotification(@Valid @RequestBody NotificationRequest request) {
        NotificationResponse response = notificationService.sendNotification(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Obtiene todas las notificaciones de un usuario específico.
     *
     * @param userId identificador del usuario (extraído de la URL)
     * @return ResponseEntity con la lista de notificaciones y código HTTP 200 (OK)
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationResponse>> getNotificationsByUserId(@PathVariable String userId) {
        List<NotificationResponse> responses = notificationService.getNotificationsByUserId(userId);
        return ResponseEntity.ok(responses);
    }

    /**
     * Marca una notificación como leída.
     *
     * @param id identificador único de la notificación (extraído de la URL)
     * @return ResponseEntity vacío con código HTTP 204 (NO CONTENT)
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable String id) {
        notificationService.markAsRead(id);
        return ResponseEntity.noContent().build();
    }
}
