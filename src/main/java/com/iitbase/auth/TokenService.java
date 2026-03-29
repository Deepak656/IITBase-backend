package com.iitbase.auth;

import com.iitbase.config.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    private final RedisTemplate<String, String> redisTemplate;
    private final JwtUtil jwtUtil;

    @Value("${jwt.expiration:604800000}") // 7 days in ms
    private long tokenExpiration;

    private static final String TOKEN_PREFIX = "token:";
    private static final String USER_TOKENS_PREFIX = "user:tokens:";

    /**
     * Store token in Redis after login
     */
    public TokenValidationResult checkToken(String jti) {
        try {
            String key = TOKEN_PREFIX + jti;
            Boolean exists = redisTemplate.hasKey(key);
            return Boolean.TRUE.equals(exists) ? TokenValidationResult.VALID : TokenValidationResult.INVALID;
        } catch (Exception e) {
            log.warn("Redis unavailable during token check - falling back to stateless JWT for JTI: {}", jti);
            return TokenValidationResult.REDIS_UNAVAILABLE;
        }
    }
    public void storeToken(String jti, String email, String role) {
        try {
            String key = TOKEN_PREFIX + jti;
            String value = email + ":" + role;

            // Store token with TTL (auto-expire)
            redisTemplate.opsForValue().set(
                    key,
                    value,
                    tokenExpiration,
                    TimeUnit.MILLISECONDS
            );

            // Also track user's active tokens (for "logout all devices")
            String userTokensKey = USER_TOKENS_PREFIX + email;
            redisTemplate.opsForSet().add(userTokensKey, jti);
            redisTemplate.expire(userTokensKey, tokenExpiration, TimeUnit.MILLISECONDS);

            log.info("Token stored in Redis - JTI: {}, Email: {}", jti, email);
        } catch (Exception e) {
            log.error("Failed to store token in Redis", e);
            // Don't fail the login - fallback to stateless JWT
        }
    }

    /**
     * Get token metadata
     */
    public String[] getTokenMetadata(String jti) {
        try {
            String key = TOKEN_PREFIX + jti;
            String value = redisTemplate.opsForValue().get(key);
            return value != null ? value.split(":") : null;
        } catch (Exception e) {
            log.error("Failed to get token metadata from Redis", e);
            return null;
        }
    }

    /**
     * Invalidate single token (logout)
     */
    public void invalidateToken(String jti, String email) {
        try {
            // Remove from active tokens
            String key = TOKEN_PREFIX + jti;
            redisTemplate.delete(key);

            // Remove from user's token set
            String userTokensKey = USER_TOKENS_PREFIX + email;
            redisTemplate.opsForSet().remove(userTokensKey, jti);

            log.info("Token invalidated - JTI: {}, Email: {}", jti, email);
        } catch (Exception e) {
            log.error("Failed to invalidate token in Redis", e);
        }
    }

    /**
     * Invalidate all user tokens (logout all devices, password change)
     */
    public void invalidateAllUserTokens(String email) {
        try {
            String userTokensKey = USER_TOKENS_PREFIX + email;

            // Get all user's tokens
            var tokens = redisTemplate.opsForSet().members(userTokensKey);

            if (tokens != null) {
                // Delete each token
                tokens.forEach(jti -> {
                    String tokenKey = TOKEN_PREFIX + jti;
                    redisTemplate.delete(tokenKey);
                });
            }

            // Clear the set
            redisTemplate.delete(userTokensKey);

            log.info("All tokens invalidated for user: {}", email);
        } catch (Exception e) {
            log.error("Failed to invalidate all user tokens", e);
        }
    }

    /**
     * Get count of active sessions for user
     */
    public long getUserActiveSessionCount(String email) {
        try {
            String userTokensKey = USER_TOKENS_PREFIX + email;
            Long count = redisTemplate.opsForSet().size(userTokensKey);
            return count != null ? count : 0;
        } catch (Exception e) {
            log.error("Failed to get active session count", e);
            return 0;
        }
    }
}