package com.iitbase.email.provider;

import com.iitbase.email.EmailMessage;
import com.iitbase.email.EmailProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Brevo (Sendinblue) Email Provider — REST (blocking)
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "email.provider", havingValue = "brevo")
@RequiredArgsConstructor
public class BrevoEmailProvider implements EmailProvider {

    private static final String BREVO_API_URL = "https://api.brevo.com/v3/smtp/email";

    private final RestTemplate restTemplate;

    @Value("${brevo.api-key:placeholder-not-set}")
    private String apiKey;

    @Value("${email.from:hello@iitbase.com}")
    private String fromEmail;

    private static final String FROM_NAME = "IITBase";

    @Override
    public void send(EmailMessage message) {

        Map<String, Object> payload = Map.of(
                "sender", Map.of(
                        "name", FROM_NAME,
                        "email", fromEmail
                ),
                "to", List.of(
                        Map.of("email", message.getTo())
                ),
                "subject", message.getSubject(),
                "htmlContent", message.getHtmlBody()
        );

        try {
            HttpEntity<Map<String, Object>> request =
                    new HttpEntity<>(payload, buildHeaders());

            restTemplate.exchange(
                    BREVO_API_URL,
                    HttpMethod.POST,
                    request,
                    Void.class
            );

            log.debug("Email sent via Brevo to: {}", message.getTo());

        } catch (Exception ex) {
            log.error("Brevo email failed for: {}", message.getTo(), ex);
            throw new RuntimeException(
                    "Email delivery failed via Brevo",
                    ex
            );
        }
    }

    // ─────────────────────────────────────────────
    // Headers builder (clean separation)
    // ─────────────────────────────────────────────

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", apiKey);
        return headers;
    }
}