package com.sigepid.notification.application.service;

import com.sigepid.notification.application.dto.NotificationRequest;
import com.sigepid.notification.application.dto.NotificationResponse;
import com.sigepid.notification.domain.entity.Notification;
import com.sigepid.notification.domain.enums.NotificationType;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Servicio de lógica de negocio para la gestión de notificaciones.
 * Almacena las notificaciones en memoria usando un ConcurrentHashMap,
 * agrupadas por userId para acceso rápido.
 *
 * Nota: Al no usar persistencia en base de datos, los datos se pierden
 * al reiniciar el servicio. Considerar integrar MongoDB o PostgreSQL para producción.
 */
@Service
public class NotificationService {

    /**
     * Almacén en memoria de notificaciones, indexado por userId.
     * Se usa ConcurrentHashMap para garantizar seguridad en entornos multi-hilo.
     */
    private final Map<String, List<Notification>> notificationStore = new ConcurrentHashMap<>();

    /**
     * Crea y almacena una nueva notificación a partir del request recibido.
     * Genera un UUID único y asigna la fecha/hora actual como timestamp de creación.
     *
     * @param request datos de la notificación a crear
     * @return respuesta con los datos de la notificación creada
     */
    public NotificationResponse sendNotification(NotificationRequest request) {
        // Construir la entidad Notification con un ID único y timestamp actual
        Notification notification = Notification.builder()
                .id(UUID.randomUUID().toString())
                .userId(request.getUserId())
                .title(request.getTitle())
                .message(request.getMessage())
                .type(request.getType())
                .read(false)
                .createdAt(LocalDateTime.now())
                .build();

        // Agregar al store; si el usuario no tiene lista, se crea una lista sincronizada
        notificationStore
                .computeIfAbsent(request.getUserId(), k -> Collections.synchronizedList(new ArrayList<>()))
                .add(notification);

        return mapToResponse(notification);
    }

    /**
     * Obtiene todas las notificaciones asociadas a un usuario específico.
     *
     * @param userId identificador del usuario
     * @return lista de notificaciones del usuario (vacía si no tiene ninguna)
     */
    public List<NotificationResponse> getNotificationsByUserId(String userId) {
        List<Notification> notifications = notificationStore.getOrDefault(userId, Collections.emptyList());
        return notifications.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Marca una notificación como leída buscándola por su ID.
     * Recorre todas las listas de notificaciones hasta encontrar la coincidencia.
     *
     * @param notificationId identificador único de la notificación a marcar
     */
    public void markAsRead(String notificationId) {
        notificationStore.values().stream()
                .flatMap(List::stream)
                .filter(n -> n.getId().equals(notificationId))
                .findFirst()
                .ifPresent(n -> n.setRead(true));
    }

    /**
     * Convierte una entidad Notification a su DTO de respuesta NotificationResponse.
     * Desacopla la capa de dominio de la capa de presentación.
     *
     * @param notification entidad de dominio a convertir
     * @return DTO con los datos de la notificación
     */
    private NotificationResponse mapToResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .userId(notification.getUserId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .read(notification.getRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
