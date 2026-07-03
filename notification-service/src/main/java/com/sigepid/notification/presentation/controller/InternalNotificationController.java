package com.sigepid.notification.presentation.controller;

import com.sigepid.notification.application.dto.NotificationRequest;
import com.sigepid.notification.application.dto.NotificationResponse;
import com.sigepid.notification.application.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para endpoints internos de la gestión de notificaciones.
 * Estos endpoints no deben ser expuestos públicamente por el API Gateway.
 */
@RestController
@RequestMapping("/internal/notifications")
@RequiredArgsConstructor
public class InternalNotificationController {

    private final NotificationService notificationService;

    /**
     * Crea y envía una nueva notificación.
     * Valida el cuerpo de la solicitud con las anotaciones de Bean Validation.
     * Este endpoint es utilizado internamente por otros microservicios (ej. order-service).
     *
     * @param request datos de la notificación a crear (validados con @Valid)
     * @return ResponseEntity con la notificación creada y código HTTP 201 (CREATED)
     */
    @PostMapping
    public ResponseEntity<NotificationResponse> sendNotification(@Valid @RequestBody NotificationRequest request) {
        NotificationResponse response = notificationService.sendNotification(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
