package com.iitbase.email.otp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * IP-level rate limiting for OTP issuance.
 *
 * Window  : 15 minutes (sliding, per IP)
 * Ceiling : 3 OTP requests per window
 *
 * This matches what's described in the resume:
 * "rate limiting (3 attempts / 15 min) to prevent OTP bombing"
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OtpRateLimitService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final int MAX_OTP_REQUESTS_PER_WINDOW = 3;
    private static final Duration WINDOW_DURATION = Duration.ofMinutes(15);

    /**
     * Throws if the IP has exhausted its OTP request quota for the current window.
     * Increments the counter on every call — intent is to count issuance attempts, not just failures.
     */
    public void checkIpRateLimit(String ipAddress) {
        String key = "otp:ratelimit:ip:" + ipAddress;
        String countStr = redisTemplate.opsForValue().get(key);
        int count = countStr != null ? Integer.parseInt(countStr) : 0;

        if (count >= MAX_OTP_REQUESTS_PER_WINDOW) {
            log.warn("OTP rate limit hit for IP: {} ({} requests in last 15 min)", ipAddress, count);
            throw new IllegalArgumentException(
                    "Too many OTP requests. Please wait 15 minutes before trying again."
            );
        }

        // Increment and (re-)set TTL so the window slides from first request
        Long newCount = redisTemplate.opsForValue().increment(key);
        if (newCount != null && newCount == 1) {
            // First request in this window — anchor the expiry
            redisTemplate.expire(key, WINDOW_DURATION);
        }
    }
}