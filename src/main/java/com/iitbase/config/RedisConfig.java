package com.iitbase.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.net.URI;
import java.time.Duration;

@Slf4j
@Configuration
public class RedisConfig {

    @Value("${redis.url}")
    private String redisUrl;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        URI uri = URI.create(redisUrl);

        String scheme   = uri.getScheme();          // "redis" or "rediss"
        String host     = uri.getHost();
        int    port     = uri.getPort() == -1 ? 6379 : uri.getPort();
        String userInfo = uri.getUserInfo();         // "default:password" or null

        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(host);
        config.setPort(port);

        if (userInfo != null && userInfo.contains(":")) {
            String[] parts = userInfo.split(":", 2);
            if (!parts[0].isBlank()) config.setUsername(parts[0]);
            if (!parts[1].isBlank()) config.setPassword(parts[1]);
        }

        boolean useSsl = "rediss".equalsIgnoreCase(scheme);

        LettuceClientConfiguration.LettuceClientConfigurationBuilder builder =
                LettuceClientConfiguration.builder()
                        .commandTimeout(Duration.ofSeconds(3));

        if (useSsl) {
            builder.useSsl();
            log.info("Redis SSL enabled (scheme={})", scheme);
        }

        log.info("Connecting to Redis — host={} port={} ssl={}", host, port, useSsl);

        return new LettuceConnectionFactory(config, builder.build());
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(LettuceConnectionFactory factory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());
        template.afterPropertiesSet();

        try {
            String pong = factory.getConnection().ping();
            log.info("Redis ping: {}", pong);
        } catch (Exception e) {
            log.error("Redis connection failed: {}", e.getMessage());
        }

        return template;
    }
}