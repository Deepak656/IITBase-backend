package com.iitbase.email.provider;

import com.iitbase.email.EmailMessage;
import com.iitbase.email.EmailProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

/**
 * Brevo (formerly Sendinblue) provider — https://brevo.com
 * Free tier: 300 emails/day, unlimited contacts.
 *
 * Activate via: email.provider=brevo
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "email.provider", havingValue = "brevo")
public class BrevoEmailProvider implements EmailProvider {

    private final WebClient webClient;
    private final String fromEmail;
    private final String fromName = "IITBase";

    public BrevoEmailProvider(
            @Value("${brevo.api-key:placeholder-not-set}") String apiKey,
            @Value("${email.from}") String fromEmail
    ) {
        this.fromEmail = fromEmail;
        this.webClient = WebClient.builder()
                .baseUrl("https://api.brevo.com/v3")
                .defaultHeader("api-key", apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public void send(EmailMessage message) {
        Map<String, Object> payload = Map.of(
                "sender", Map.of("name", fromName, "email", fromEmail),
                "to", List.of(Map.of("email", message.getTo())),
                "subject", message.getSubject(),
                "htmlContent", message.getHtmlBody()
        );

        try {
            webClient.post()
                    .uri("/smtp/email")
                    .bodyValue(payload)
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            log.debug("Email dispatched via Brevo to: {}", message.getTo());
        } catch (Exception ex) {
            log.error("Brevo failed for: {}", message.getTo(), ex);
            throw new RuntimeException("Email delivery failed via Brevo: " + ex.getMessage(), ex);
        }
    }
}