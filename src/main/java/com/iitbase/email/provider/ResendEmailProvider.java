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

import java.util.Map;

/**
 * Resend provider — https://resend.com
 * Free tier: 3,000 emails/month, 100/day. No CC required.
 *
 * Activate via: email.provider=resend
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "email.provider", havingValue = "resend")
public class ResendEmailProvider implements EmailProvider {

    private final WebClient webClient;
    private final String fromEmail;

    public ResendEmailProvider(
            @Value("${resend.api-key:placeholder-not-set}") String apiKey,
            @Value("${email.from:hello@iitbase.com}") String fromEmail  // reuse same property, same sender
    ) {
        this.fromEmail = fromEmail;
        this.webClient = WebClient.builder()
                .baseUrl("https://api.resend.com")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public void send(EmailMessage message) {
        Map<String, Object> payload = Map.of(
                "from", "IITBase <" + fromEmail + ">",
                "to", new String[]{message.getTo()},
                "subject", message.getSubject(),
                "html", message.getHtmlBody(),
                "text", message.getTextBody() != null ? message.getTextBody() : ""
        );

        try {
            webClient.post()
                    .uri("/emails")
                    .bodyValue(payload)
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            log.debug("Email dispatched via Resend to: {}", message.getTo());
        } catch (Exception ex) {
            log.error("Resend failed for: {}", message.getTo(), ex);
            throw new RuntimeException("Email delivery failed via Resend: " + ex.getMessage(), ex);
        }
    }
}