package com.iitbase.notification.controller;

import com.iitbase.common.ApiResponse;
import com.iitbase.notification.dto.response.NotificationResponse;
import com.iitbase.notification.dto.response.UnreadCountResponse;
import com.iitbase.notification.service.NotificationService;
import com.iitbase.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(ApiResponse.success(
                notificationService.getNotifications(user.getId(), page, size)
        ));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<UnreadCountResponse>> getUnreadCount(
            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(ApiResponse.success(
                notificationService.getUnreadCount(user.getId())
        ));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markOneAsRead(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        notificationService.markOneAsRead(user.getId(), id);
        return ResponseEntity.ok(ApiResponse.success(null, "Marked as read"));
    }

    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(
            @AuthenticationPrincipal User user) {

        notificationService.markAllAsRead(user.getId());
        return ResponseEntity.ok(ApiResponse.success(null, "All marked as read"));
    }
}