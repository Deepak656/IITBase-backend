package com.iitbase.notification.dto.response;

import com.iitbase.notification.entity.Notification;
import com.iitbase.notification.enums.NotificationType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
public class NotificationResponse {
    private Long id;
    private NotificationType type;
    private String title;
    private String message;
    private Map<String, Object> payload;   // frontend uses for deep linking
    private Boolean isRead;
    private LocalDateTime createdAt;

    public static NotificationResponse from(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .type(n.getType())
                .title(n.getTitle())
                .message(n.getMessage())
                .payload(n.getPayload())
                .isRead(n.getIsRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}