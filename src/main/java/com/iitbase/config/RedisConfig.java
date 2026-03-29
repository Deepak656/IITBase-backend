package com.iitbase.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Slf4j
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
        log.info("Redis URL from ENV: {}", System.getenv("SPRING_REDIS_URL"));
        // 🔍 Test connection at startup
        try {
            String pong = factory.getConnection().ping();
            log.info("✅ Redis connected successfully: {}", pong);
        } catch (Exception e) {
            log.error("❌ Redis connection FAILED. Check Upstash URL / SSL. Error: {}", e.getMessage());
        }

        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // 🔥 Important serializers
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());

        template.afterPropertiesSet();
        return template;
    }
}