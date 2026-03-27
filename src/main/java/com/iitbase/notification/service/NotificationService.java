package com.iitbase.notification.service;

import com.iitbase.notification.dto.response.NotificationResponse;
import com.iitbase.notification.dto.response.UnreadCountResponse;
import com.iitbase.notification.entity.Notification;
import com.iitbase.notification.enums.NotificationType;
import com.iitbase.notification.exception.NotificationNotFoundException;
import com.iitbase.notification.repository.NotificationRepository;
import com.iitbase.user.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository    notificationRepository;
    private final NotificationRedisService  redisService;
    private final NotificationSseService    sseService;

    /**
     * Core method — all listeners call this.
     * Creates DB record, increments Redis, pushes via SSE if connected.
     */
    public void create(Long recipientId,
                       UserRole recipientRole,
                       NotificationType type,
                       String title,
                       String message,
                       Map<String, Object> payload) {

        Notification notification = Notification.builder()
                .recipientId(recipientId)
                .recipientRole(recipientRole)
                .type(type)
                .title(title)
                .message(message)
                .payload(payload)
                .isRead(false)
                .build();

        Notification saved = notificationRepository.save(notification);

        // Increment Redis unread counter — O(1), no DB query needed for badge
        redisService.increment(recipientId);

        // Push via SSE if user is currently connected
        sseService.pushNotification(recipientId, NotificationResponse.from(saved));

        log.info("Notification created: recipientId={} type={}", recipientId, type);
    }

    // ── Read operations ───────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Map<String, Object> getNotifications(Long recipientId,
                                                int page, int size) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Notification> notifPage = notificationRepository
                .findByRecipientIdOrderByCreatedAtDesc(recipientId, pageable);

        return Map.of(
                "notifications", notifPage.getContent().stream()
                        .map(NotificationResponse::from).toList(),
                "currentPage",   notifPage.getNumber(),
                "totalItems",    notifPage.getTotalElements(),
                "totalPages",    notifPage.getTotalPages()
        );
    }

    @Transactional(readOnly = true)
    public UnreadCountResponse getUnreadCount(Long recipientId) {
        Long count = redisService.getUnreadCount(recipientId);

        if (count == null) {
            // Redis miss — fall back to DB and re-sync Redis
            count = notificationRepository
                    .countByRecipientIdAndIsReadFalse(recipientId);
            redisService.syncCount(recipientId, count);
            log.info("Redis re-synced for userId={} count={}", recipientId, count);
        }

        return UnreadCountResponse.builder().unreadCount(count).build();
    }

    public void markOneAsRead(Long recipientId, Long notificationId) {
        int updated = notificationRepository.markOneAsRead(notificationId);

        if (updated == 0) {
            throw new NotificationNotFoundException(notificationId);
        }

        redisService.decrement(recipientId);
    }

    public void markAllAsRead(Long recipientId) {
        int updated = notificationRepository.markAllAsRead(recipientId);
        if (updated > 0) {
            redisService.resetCount(recipientId);
        }
        log.info("All notifications marked read: recipientId={} count={}",
                recipientId, updated);
    }
}