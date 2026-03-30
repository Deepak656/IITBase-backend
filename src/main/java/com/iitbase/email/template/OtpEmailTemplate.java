package com.iitbase.email.template;

import com.iitbase.email.otp.OtpPurpose;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OtpEmailTemplate {

    private final EmailTemplateShell s;

    // ── Subject line ──────────────────────────────────────────────────────
    // Format: "<otp> is your IITBase <action>" — putting the OTP first means
    // it shows in the inbox notification snippet, which improves open rates
    // and is a pattern used by Stripe, Linear, etc.

    public String subject(String otp, OtpPurpose purpose) {
        return switch (purpose) {
            case SIGNUP               -> otp + " is your IITBase verification code";
            case RESET_PASSWORD       -> otp + " — reset your IITBase password";
            case CHANGE_EMAIL         -> otp + " — verify your new email address";
            case VERIFY_CURRENT_EMAIL -> otp + " — confirm your current email";
        };
    }

    // ── Keep old signature for callers that don't pass otp into subject ───
    public String subject(OtpPurpose purpose) {
        return switch (purpose) {
            case SIGNUP               -> "Your IITBase verification code";
            case RESET_PASSWORD       -> "Reset your IITBase password";
            case CHANGE_EMAIL         -> "Verify your new email address";
            case VERIFY_CURRENT_EMAIL -> "Confirm your current email";
        };
    }

    // ── Heading copy per purpose ──────────────────────────────────────────
    private String heading(OtpPurpose purpose) {
        return switch (purpose) {
            case SIGNUP               -> "Verify your email address";
            case RESET_PASSWORD       -> "Reset your password";
            case CHANGE_EMAIL         -> "Verify your new email";
            case VERIFY_CURRENT_EMAIL -> "Confirm your current email";
        };
    }

    private String bodyText(OtpPurpose purpose) {
        return switch (purpose) {
            case SIGNUP ->
                    "Use the code below to complete your signup. Valid for <strong style=\"color:#94a3b8;\">10 minutes</strong>.";
            case RESET_PASSWORD ->
                    "Use the code below to reset your IITBase password. Valid for <strong style=\"color:#94a3b8;\">10 minutes</strong>.";
            case CHANGE_EMAIL ->
                    "Use the code below to verify your new email address. Valid for <strong style=\"color:#94a3b8;\">10 minutes</strong>.";
            case VERIFY_CURRENT_EMAIL ->
                    "Use the code below to confirm your current email address. Valid for <strong style=\"color:#94a3b8;\">10 minutes</strong>.";
        };
    }

    // ── HTML body ─────────────────────────────────────────────────────────

    public String html(String otp, OtpPurpose purpose) {
        String content = s.eyebrow("Verification")
                + s.heading(heading(purpose))
                + s.subtext(bodyText(purpose))
                + otpBlock(otp)
                + s.divider()
                + s.note("IITBase will never ask for your OTP over call or chat. "
                + "If you didn't request this, you can safely ignore this email "
                + "— your account is not at risk.");
        return s.shell(content);
    }

    // ── Plain-text fallback ───────────────────────────────────────────────

    public String text(String otp, OtpPurpose purpose) {
        return """
            %s
            
            Your verification code: %s
            
            This code is valid for 10 minutes. Do not share it with anyone.
            IITBase will never ask for your OTP over call or chat.
            
            If you didn't request this, you can safely ignore this email.
            
            — The IITBase team
            https://iitbase.com
            """.formatted(heading(purpose), otp);
    }

    // ── OTP display block ─────────────────────────────────────────────────

    private String otpBlock(String otp) {
        // Space out digits for readability in all clients
        String spaced = String.join(" ", otp.split(""));
        return """
            <div style="background:rgba(99,102,241,0.06);border:1px solid rgba(99,102,241,0.20);
                        border-radius:14px;padding:28px;text-align:center;margin-bottom:24px;">
              <p style="font-size:38px;font-weight:700;letter-spacing:10px;color:#f8fafc;
                        font-variant-numeric:tabular-nums;margin:0 0 10px;
                        font-family:-apple-system,BlinkMacSystemFont,'Courier New',monospace;">
                %s
              </p>
              <p style="margin:0;font-size:12px;color:#475569;
                        font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Arial,sans-serif;">
                &#9679;&nbsp; Expires in 10 minutes
              </p>
            </div>
            """.formatted(spaced);
    }
}