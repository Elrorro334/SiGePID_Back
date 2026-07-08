package com.sigepid.notification.application.service;

import com.sigepid.notification.application.dto.NotificationRequest;
import com.sigepid.notification.application.dto.NotificationResponse;
import com.sigepid.notification.domain.entity.Notification;
import com.sigepid.notification.domain.enums.NotificationType;
import org.springframework.stereotype.Service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final JavaMailSender mailSender;
    private final com.sigepid.notification.infrastructure.client.AuthClient authClient;
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

        String email = request.getUserEmail();
        if (email == null || email.isEmpty()) {
            try {
                // Feign client is needed here, so AuthClient should be injected.
                Map<String, Object> profile = authClient.getProfile(request.getUserId());
                if (profile != null && profile.containsKey("email")) {
                    email = (String) profile.get("email");
                }
            } catch (Exception e) {
                log.warn("Could not fetch user email for user {}: {}", request.getUserId(), e.getMessage());
            }
        }

        if (email != null && !email.isEmpty()) {
            try {
                jakarta.mail.internet.MimeMessage mimeMessage = mailSender.createMimeMessage();
                org.springframework.mail.javamail.MimeMessageHelper helper = new org.springframework.mail.javamail.MimeMessageHelper(mimeMessage, "utf-8");
                
                String htmlMsg = "<div style='font-family: \"Segoe UI\", Roboto, Helvetica, Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 30px; border: 1px solid #e2e8f0; border-radius: 16px; background-color: #ffffff;'>"
                        + "<div style='text-align: center; margin-bottom: 30px;'>"
                        + "<div style='display: inline-flex; align-items: center; justify-content: center; width: 60px; height: 60px; background-color: #10b981; border-radius: 12px; color: white; font-size: 24px; font-weight: bold;'>S</div>"
                        + "<h2 style='color: #10b981; margin: 15px 0 0 0; font-size: 24px;'>SiGePID</h2>"
                        + "</div>"
                        + "<div style='background-color: #f8fafc; padding: 25px; border-radius: 12px; border-left: 4px solid #10b981;'>"
                        + "<h3 style='color: #0f172a; margin-top: 0; font-size: 20px;'>" + request.getTitle() + "</h3>"
                        + "<p style='color: #475569; font-size: 16px; line-height: 1.6; margin-bottom: 0;'>" + request.getMessage() + "</p>"
                        + "</div>"
                        + "<div style='text-align: center; margin-top: 30px; padding-top: 20px; border-top: 1px solid #e2e8f0; color: #94a3b8; font-size: 13px;'>"
                        + "<p style='margin: 0;'>Sistema de Gestión de Pedidos e Inventario Distribuido</p>"
                        + "<p style='margin: 5px 0 0 0;'>Este es un mensaje automático, por favor no responda.</p>"
                        + "</div>"
                        + "</div>";

                helper.setFrom("sanxcruro122@gmail.com");
                helper.setTo(email);
                helper.setSubject("SiGePID: " + request.getTitle());
                helper.setText(htmlMsg, true);
                
                mailSender.send(mimeMessage);
                log.info("HTML Email sent to {}", email);
            } catch (Exception e) {
                log.error("Failed to send HTML email to {}: {}", email, e.getMessage());
            }
        }

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
