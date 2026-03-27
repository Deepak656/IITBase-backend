package com.iitbase.notification.entity;

import com.iitbase.common.BaseEntity;
import com.iitbase.notification.enums.NotificationType;
import com.iitbase.user.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Entity
@Table(
        name = "notifications",
        indexes = {
                @Index(name = "idx_notif_recipient_unread",
                        columnList = "recipient_id, is_read, created_at")
        }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Notification extends BaseEntity {

    @Column(nullable = false)
    private Long recipientId;

    // Needed to route notification to correct UI
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole recipientRole;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String message;

    // JSONB — flexible metadata for deep linking
    // e.g. { "applicationId": 12, "jobId": 5, "companyName": "Acme" }
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> payload;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isRead = false;
}