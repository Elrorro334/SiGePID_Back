package com.sigepid.notification.application.dto;

import com.sigepid.notification.domain.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private String id;
    private String userId;
    private String title;
    private String message;
    private NotificationType type;
    private Boolean read;
    private LocalDateTime createdAt;
}
