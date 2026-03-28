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
        // Eagerly test the connection so startup logs tell you immediately
        // whether Redis is reachable — instead of finding out on the first request.
        try {
            factory.getConnection().ping();
            log.info("Redis connection established successfully");
        } catch (Exception e) {
            log.error("Redis connection FAILED at startup — check REDISHOST/REDISPORT/REDISPASSWORD env vars. Error: {}", e.getMessage());
            // Not throwing — app boots, falls back to stateless JWT per our filter logic
        }

        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }
}