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
    private final Map<String, List<Notification>> notificationStore = new ConcurrentHashMap<>();

    public NotificationResponse sendNotification(NotificationRequest request) {
        Notification notification = Notification.builder()
                .id(UUID.randomUUID().toString())
                .userId(request.getUserId())
                .title(request.getTitle())
                .message(request.getMessage())
                .type(request.getType())
                .read(false)
                .createdAt(LocalDateTime.now())
                .build();

        notificationStore
                .computeIfAbsent(request.getUserId(), k -> Collections.synchronizedList(new ArrayList<>()))
                .add(notification);

        if (request.getUserEmail() != null && !request.getUserEmail().isEmpty()) {
            try {
                SimpleMailMessage mailMessage = new SimpleMailMessage();
                mailMessage.setFrom("sanxcruro122@gmail.com");
                mailMessage.setTo(request.getUserEmail());
                mailMessage.setSubject("SiGePID: " + request.getTitle());
                mailMessage.setText(request.getMessage());
                mailSender.send(mailMessage);
                log.info("Email sent to {}", request.getUserEmail());
            } catch (Exception e) {
                log.error("Failed to send email to {}: {}", request.getUserEmail(), e.getMessage());
            }
        }

        return mapToResponse(notification);
    }

    public List<NotificationResponse> getNotificationsByUserId(String userId) {
        List<Notification> notifications = notificationStore.getOrDefault(userId, Collections.emptyList());
        return notifications.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public void markAsRead(String notificationId) {
        notificationStore.values().stream()
                .flatMap(List::stream)
                .filter(n -> n.getId().equals(notificationId))
                .findFirst()
                .ifPresent(n -> n.setRead(true));
    }

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
