package com.iitbase.email.provider;

import com.iitbase.email.EmailMessage;
import com.iitbase.email.EmailProvider;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * SendGrid provider.
 * Activate via: email.provider=sendgrid
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "email.provider", havingValue = "sendgrid")
@RequiredArgsConstructor
public class SendGridEmailProvider implements EmailProvider {

    private final SendGrid sendGrid;

    @Value("${email.from}")
    private String fromEmail;

    @Override
    public void send(EmailMessage message) {
        Email from = new Email(fromEmail, "IITBase");
        Email to = new Email(message.getTo());
        Content content = new Content("text/html", message.getHtmlBody());
        Mail mail = new Mail(from, message.getSubject(), to, content);

        try {
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            var response = sendGrid.api(request);
            if (response.getStatusCode() >= 400) {
                throw new RuntimeException("SendGrid returned HTTP " + response.getStatusCode());
            }

            log.debug("Email dispatched via SendGrid to: {}", message.getTo());
        } catch (Exception ex) {
            log.error("SendGrid failed for: {}", message.getTo(), ex);
            throw new RuntimeException("Email delivery failed via SendGrid: " + ex.getMessage(), ex);
        }
    }
}