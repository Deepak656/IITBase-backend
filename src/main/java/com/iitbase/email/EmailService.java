package com.iitbase.email;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final SendGrid sendGrid;
    @Value("${sendgrid.from-email}")
    private String fromEmail;

    public void sendOtp(String to, String otp, OtpPurpose purpose) {
        Email from = new Email(fromEmail, "IITBase");
        Email toEmail = new Email(to);
        Content content = new Content(
                "text/html",
                "Your IITBase.com OTP is: " + otp + "\nValid for 10 minutes."
        );
        String subject = purpose == OtpPurpose.SIGNUP
                ? "Verify your IITBase account"
                : "Reset your IITBase password";
        Mail mail = new Mail(from, subject, toEmail, content);

        try {
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            var response = sendGrid.api(request);
            // Log the response for debugging
            if (response.getStatusCode() >= 400) {
                throw new RuntimeException("SendGrid returned: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to send OTP email to " + to + ": " + e.getMessage(), e);
        }
    }
    private String buildEmailTemplate(String otp, OtpPurpose purpose) {
        return """
            <!DOCTYPE html>
            <html>
            <body style="font-family: -apple-system, system-ui, sans-serif; background: #f8fafc; padding: 40px 20px;">
                <div style="max-width: 600px; margin: 0 auto; background: white; border-radius: 16px; padding: 48px; box-shadow: 0 4px 6px rgba(0,0,0,0.1);">
                    <div style="text-align: center; margin-bottom: 32px;">
                        <div style="width: 64px; height: 64px; background: linear-gradient(135deg, #2563eb, #1d4ed8); border-radius: 16px; display: inline-flex; align-items: center; justify-content: center; margin-bottom: 16px;">
                            <span style="color: white; font-size: 32px; font-weight: bold;">I</span>
                        </div>
                        <h1 style="color: #0f172a; margin: 0; font-size: 28px;">Your verification code</h1>
                    </div>
           
                    <div style="background: #f1f5f9; border-radius: 12px; padding: 24px; text-align: center; margin-bottom: 24px;">
                        <div style="font-size: 36px; font-weight: bold; letter-spacing: 8px; color: #0f172a;">%s</div>
                    </div>
                    
                    <p style="color: #64748b; font-size: 14px; line-height: 1.6; margin: 0 0 16px;">
                        This code expires in <strong>10 minutes</strong>. Don't share this code with anyone.
                    </p>
                    
                    <p style="color: #64748b; font-size: 14px; line-height: 1.6; margin: 0;">
                        If you didn't request this code, please ignore this email.
                    </p>
                    
                    <div style="margin-top: 32px; padding-top: 24px; border-top: 1px solid #e2e8f0; text-align: center;">
                        <p style="color: #94a3b8; font-size: 12px; margin: 0;">
                            © 2026 IITBase. All rights reserved.
                        </p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(otp);
    }
}
