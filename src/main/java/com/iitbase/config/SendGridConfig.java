package com.iitbase.config;

import com.sendgrid.SendGrid;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SendGridConfig {

    @Value("${sendgrid.api.key}")
    private String apiKey;

    @Bean
    public SendGrid sendGrid() {
        return new SendGrid(apiKey);
    }
    @PostConstruct
    void validate() {
        if (apiKey == null) {
            throw new IllegalStateException("SendGrid not configured");
        }
    }

}
