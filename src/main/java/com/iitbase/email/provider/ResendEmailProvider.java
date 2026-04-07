package com.iitbase.email.provider;

import com.iitbase.email.EmailMessage;
import com.iitbase.email.EmailProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Resend Email Provider — REST (blocking)
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "email.provider", havingValue = "resend")
public class ResendEmailProvider implements EmailProvider {

    private static final String RESEND_API_URL = "https://api.resend.com/emails";

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String fromEmail;

    public ResendEmailProvider(
            RestTemplate restTemplate,
            @Value("${resend.api-key:placeholder-not-set}") String apiKey,
            @Value("${email.from:hello@iitbase.com}") String fromEmail
    ) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
        this.fromEmail = fromEmail;
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
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<Map<String, Object>> request =
                    new HttpEntity<>(payload, headers);

            restTemplate.exchange(
                    RESEND_API_URL,
                    HttpMethod.POST,
                    request,
                    Void.class
            );

            log.debug("Email dispatched via Resend to: {}", message.getTo());

        } catch (Exception ex) {
            log.error("Resend failed for: {}", message.getTo(), ex);
            throw new RuntimeException(
                    "Email delivery failed via Resend: " + ex.getMessage(), ex
            );
        }
    }
}