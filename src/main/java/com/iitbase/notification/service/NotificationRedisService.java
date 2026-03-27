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
        redisTemplate.opsForValue().increment(KEY_PREFIX + userId);
    }

    public void decrement(Long userId) {
        String key = KEY_PREFIX + userId;
        Long current = getCurrentCount(userId);
        // Never go below 0
        if (current != null && current > 0) {
            redisTemplate.opsForValue().decrement(key);
        }
    }

    public Long getUnreadCount(Long userId) {
        Long count = getCurrentCount(userId);
        if (count != null) return count;

        // Redis miss — this shouldn't happen often
        // Caller should fall back to DB count and re-sync
        log.warn("Redis miss for unread count userId={}", userId);
        return null;
    }

    public void resetCount(Long userId) {
        redisTemplate.opsForValue().set(KEY_PREFIX + userId, "0");
    }

    public void syncCount(Long userId, long dbCount) {
        redisTemplate.opsForValue().set(KEY_PREFIX + userId,
                String.valueOf(dbCount));
    }

    private Long getCurrentCount(Long userId) {
        String value = redisTemplate.opsForValue().get(KEY_PREFIX + userId);
        return value != null ? Long.parseLong(value) : null;
    }
}