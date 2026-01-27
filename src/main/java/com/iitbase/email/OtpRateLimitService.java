package com.iitbase.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpRateLimitService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final int MAX_REQUESTS_PER_IP_PER_HOUR = 10;

    public void checkIpRateLimit(String ipAddress) {
        String key = "otp:ratelimit:ip:" + ipAddress;
        String countStr = redisTemplate.opsForValue().get(key);
        int count = countStr != null ? Integer.parseInt(countStr) : 0;

        if (count >= MAX_REQUESTS_PER_IP_PER_HOUR) {
            log.warn("Rate limit exceeded for IP: {}", ipAddress);
            throw new IllegalArgumentException("Too many OTP requests from this IP. Please try later.");
        }

        redisTemplate.opsForValue().increment(key);
        if (count == 0) {
            redisTemplate.expire(key, Duration.ofHours(1));
        }
    }
}
