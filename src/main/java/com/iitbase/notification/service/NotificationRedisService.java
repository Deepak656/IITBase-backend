package com.iitbase.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationRedisService {

    private final StringRedisTemplate redisTemplate;
    private static final String KEY_PREFIX = "notif:unread:";

    public void increment(Long userId) {
        try {
            redisTemplate.opsForValue().increment(KEY_PREFIX + userId);
        } catch (Exception e) {
            log.warn("Redis unavailable — skipping unread increment for userId={}", userId);
        }
    }

    public void decrement(Long userId) {
        try {
            Long current = getCurrentCount(userId);
            if (current != null && current > 0) {
                redisTemplate.opsForValue().decrement(KEY_PREFIX + userId);
            }
        } catch (Exception e) {
            log.warn("Redis unavailable — skipping unread decrement for userId={}", userId);
        }
    }

    public Long getUnreadCount(Long userId) {
        Long count = getCurrentCount(userId);
        if (count == null) {
            log.debug("Redis miss for unread count userId={} — caller should fall back to DB", userId);
        }
        return count;
    }

    public void resetCount(Long userId) {
        try {
            redisTemplate.opsForValue().set(KEY_PREFIX + userId, "0");
        } catch (Exception e) {
            log.warn("Redis unavailable — skipping unread reset for userId={}", userId);
        }
    }

    public void syncCount(Long userId, long dbCount) {
        try {
            redisTemplate.opsForValue().set(KEY_PREFIX + userId, String.valueOf(dbCount));
        } catch (Exception e) {
            log.warn("Redis unavailable — skipping unread sync for userId={}", userId);
        }
    }

    private Long getCurrentCount(Long userId) {
        try {
            String value = redisTemplate.opsForValue().get(KEY_PREFIX + userId);
            return value != null ? Long.parseLong(value) : null;
        } catch (Exception e) {
            log.warn("Redis unavailable — cannot get unread count for userId={}", userId);
            return null;
        }
    }
}