package com.iitbase.email.template;

import com.iitbase.email.otp.OtpPurpose;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OtpEmailTemplate {

    private final EmailTemplateShell s;

    // ── Subject line ──────────────────────────────────────────────────────
    // OTP first → shows in notification snippet before subject is truncated.
    // Pattern used by Stripe, Linear, Vercel.

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
    // Deliberately short — "Verify your email address" wraps to 2 lines on
    // narrow mobile screens and orphans "address" alone. Shorter headings
    // stay on one line on all screens ≥320px wide.

    private String heading(OtpPurpose purpose) {
        return switch (purpose) {
            case SIGNUP               -> "Verify your email";
            case RESET_PASSWORD       -> "Reset your password";
            case CHANGE_EMAIL         -> "Verify your new email";
            case VERIFY_CURRENT_EMAIL -> "Confirm your email";
        };
    }

    // ── Body text per purpose ─────────────────────────────────────────────
    // "Enter" is more action-oriented than "Use". Sentence split after the
    // action so the validity note sits on its own clause — easier to scan.

    private String bodyText(OtpPurpose purpose) {
        return switch (purpose) {
            case SIGNUP ->
                    "Enter the code below to complete your signup. "
                            + "Valid for <strong style=\"color:#94a3b8;\">10 minutes</strong>.";
            case RESET_PASSWORD ->
                    "Enter the code below to reset your IITBase password. "
                            + "Valid for <strong style=\"color:#94a3b8;\">10 minutes</strong>.";
            case CHANGE_EMAIL ->
                    "Enter the code below to verify your new email address. "
                            + "Valid for <strong style=\"color:#94a3b8;\">10 minutes</strong>.";
            case VERIFY_CURRENT_EMAIL ->
                    "Enter the code below to confirm your current email address. "
                            + "Valid for <strong style=\"color:#94a3b8;\">10 minutes</strong>.";
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
    //
    // Design goals:
    //   1. Digits never wrap across lines on any mobile client.
    //   2. Copy-paste produces the raw OTP with zero spaces — backend
    //      validation works without any server-side stripping.
    //   3. Looks considered and intentional, not auto-generated.
    //
    // How:
    //   - Each digit in its own <td>. Table rows never break mid-row, so
    //     wrapping is physically impossible on any screen width.
    //   - Visual gap is td padding only — no space characters are injected
    //     between digits. Clipboard value = raw OTP (e.g. "482917").
    //   - font-size reduced to 32px from 40px — less toy-like, more refined.
    //   - Removed "ONE-TIME CODE" uppercase label — redundant given the
    //     heading above. Replaced with a quieter contextual line.
    //   - Outer box has a left accent border (3px indigo) instead of a
    //     full border — signals brand without boxing the content in.
    //   - Expiry note upgraded to a proper pill badge — clearly scannable.
    //   - user-select:text set explicitly — Samsung Mail and older Outlook
    //     Mobile default to user-select:none which silently breaks
    //     tap-to-select on some elements.

    private String otpBlock(String otp) {
        // One <td> per digit — no spaces between nodes, no clipboard pollution
        StringBuilder digitCells = new StringBuilder();
        for (char c : otp.toCharArray()) {
            digitCells.append("""
                <td style="
                    padding: 0 6px;
                    font-size: 32px;
                    font-weight: 700;
                    color: #f8fafc;
                    font-family: 'Courier New', Courier, monospace;
                    font-variant-numeric: tabular-nums;
                    letter-spacing: 0;
                    line-height: 1;
                    -webkit-user-select: text;
                    -moz-user-select: text;
                    -ms-user-select: text;
                    user-select: text;
                ">%c</td>
                """.formatted(c));
        }

        return """
            <!-- ── OTP Block ─────────────────────────────────────── -->
            <!--
              Left accent border: the 3px indigo left border is the primary
              brand signal. Combined with a subtly lighter background than
              the card shell, it creates clear layer hierarchy without needing
              a heavy full border.

              Digit table: border-collapse:separate + cellspacing gives us
              reliable inter-digit spacing in Outlook (which ignores padding
              on inline elements but respects table cell geometry).
            -->
            <table role="presentation" width="100%%" cellpadding="0" cellspacing="0"
                   style="background: rgba(99,102,241,0.08);
                          border-left: 3px solid #6366f1;
                          border-radius: 0 12px 12px 0;
                          margin-bottom: 28px;">
              <tr>
                <td style="padding: 24px 28px 22px;">

                  <!-- Context label — quiet, not shouting -->
                  <p style="margin: 0 0 16px;
                             font-size: 11px;
                             font-weight: 500;
                             letter-spacing: 0.5px;
                             color: #64748b;
                             font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Arial, sans-serif;">
                    Your one-time code
                  </p>

                  <!--
                    Digit row.
                    - No text nodes between <td>s means clipboard = raw OTP.
                    - cellspacing="4" handles Outlook inter-digit gap reliably.
                    - align="left" so it doesn't recentre on narrow Outlook widths.
                  -->
                  <table role="presentation" align="left" cellpadding="0" cellspacing="4"
                         style="margin: 0 0 18px; border-collapse: separate;">
                    <tr>
                      %s
                    </tr>
                  </table>

                  <!-- Expiry pill -->
                  <table role="presentation" cellpadding="0" cellspacing="0">
                    <tr>
                      <td style="background: rgba(99,102,241,0.10);
                                 border: 1px solid rgba(99,102,241,0.22);
                                 border-radius: 20px;
                                 padding: 4px 12px;">
                        <p style="margin: 0;
                                   font-size: 12px;
                                   color: #64748b;
                                   font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Arial, sans-serif;">
                          Expires in&nbsp;<strong style="color: #94a3b8;">10 minutes</strong>
                        </p>
                      </td>
                    </tr>
                  </table>

                </td>
              </tr>
            </table>
            <!-- ── /OTP Block ────────────────────────────────────── -->
            """.formatted(digitCells.toString());
    }
}