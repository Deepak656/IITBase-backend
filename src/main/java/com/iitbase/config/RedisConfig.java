package com.iitbase.config;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.SslOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Slf4j
@Configuration
public class RedisConfig {

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {

        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName("relaxed-airedale-87540.upstash.io");
        redisConfig.setPort(6379);
        redisConfig.setUsername("default");
        redisConfig.setPassword("gQAAAAAAAVX0AAIncDJmMzEwOGViYzJmMTg0YjFkOTY1ZGJjYmYxNjA2MDQxNHAyODc1NDA"); // 🔴 put real password here

        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()

                // ✅ Put everything BEFORE useSsl()
                .commandTimeout(Duration.ofSeconds(2))
                .shutdownTimeout(Duration.ZERO)

                .clientOptions(ClientOptions.builder()
                        .autoReconnect(true)
                        .disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS)
                        .build()
                )

                // ✅ SSL LAST
                .useSsl()
                .disablePeerVerification()
                .build();

        return new LettuceConnectionFactory(redisConfig, clientConfig);
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {

        try {
            var connection = factory.getConnection();
            String pong = connection.ping();
            log.info("✅ Redis connected: {}", pong);
            connection.close();
        } catch (Exception e) {
            log.error("❌ Redis connection FAILED: {}", e.getMessage(), e);
        }

        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());

        template.afterPropertiesSet();
        return template;
    }
}