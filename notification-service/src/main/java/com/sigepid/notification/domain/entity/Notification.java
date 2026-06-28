package com.sigepid.notification.domain.entity;

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
public class Notification {

    private String id;
    private String userId;
    private String title;
    private String message;
    private NotificationType type;

    @Builder.Default
    private Boolean read = false;

    private LocalDateTime createdAt;
}
