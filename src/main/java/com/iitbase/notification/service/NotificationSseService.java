package com.iitbase.notification.service;

import com.iitbase.notification.dto.response.NotificationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class NotificationSseService {

    // One emitter per logged-in user
    // ConcurrentHashMap — thread-safe for concurrent connections
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter register(Long userId) {
        // 5 min timeout — client reconnects automatically
        SseEmitter emitter = new SseEmitter(300_000L);

        emitter.onCompletion(() -> {
            emitters.remove(userId);
            log.debug("SSE connection closed: userId={}", userId);
        });

        emitter.onTimeout(() -> {
            emitters.remove(userId);
            log.debug("SSE connection timed out: userId={}", userId);
        });

        emitter.onError(ex -> {
            emitters.remove(userId);
            log.debug("SSE connection error: userId={}", userId);
        });

        emitters.put(userId, emitter);
        log.debug("SSE connection registered: userId={}", userId);
        return emitter;
    }

    public void pushNotification(Long userId, NotificationResponse notification) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter == null) return;    // user not connected — polling will catch it

        try {
            emitter.send(SseEmitter.event()
                    .name("notification")
                    .data(notification));
        } catch (IOException ex) {
            emitters.remove(userId);
            log.debug("SSE push failed, removed emitter: userId={}", userId);
        }
    }
}