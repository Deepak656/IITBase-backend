package com.iitbase.email.template;

import com.iitbase.email.otp.OtpPurpose;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OtpEmailTemplate {

    private final EmailTemplateShell s;

    public String subject(OtpPurpose purpose) {
        return switch (purpose) {
            case SIGNUP               -> "Your IITBase verification code";
            case RESET_PASSWORD       -> "Reset your IITBase password";
            case CHANGE_EMAIL         -> "Verify your new email address";
            case VERIFY_CURRENT_EMAIL -> "Confirm your current email";
        };
    }

    public String html(String otp, String subject) {
        String content = """
            <h2 style="margin:0 0 6px;font-size:22px;font-weight:600;color:#0f172a;
                       font-family:Georgia,serif;letter-spacing:-0.3px;">%s</h2>
            <p style="color:#64748b;font-size:14px;margin:0 0 28px;line-height:1.6;font-weight:300;">
              Use the code below to continue. It expires in <strong>10 minutes</strong>.
            </p>
            <div style="background:#f8fafc;border:1px solid #e2e8f0;border-radius:12px;
                        padding:24px;text-align:center;margin-bottom:24px;">
              <span style="font-size:38px;font-weight:700;letter-spacing:10px;
                           color:#0f172a;font-variant-numeric:tabular-nums;">%s</span>
            </div>
            <p style="color:#94a3b8;font-size:13px;margin:0;line-height:1.6;">
              Don't share this code — IITBase will never ask for your OTP.
              If you didn't request this, ignore this email.
            </p>
            """.formatted(subject, otp);
        return s.shell(content);
    }

    public String text(String otp) {
        return "Your IITBase OTP is: " + otp + ". Valid for 10 minutes. Do not share this code.";
    }
}