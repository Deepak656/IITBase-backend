package com.iitbase.email.template;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JobseekerEmailTemplate {

    private final EmailTemplateShell s;

    // ── Profile verified ──────────────────────────────────────────────────

    public String profileVerifiedHtml(String name) {
        String content = """
            <h2 style="margin:0 0 6px;font-size:22px;font-weight:600;color:#0f172a;
                       font-family:Georgia,serif;letter-spacing:-0.3px;">
              Your profile is verified, %s 🎉
            </h2>
            <p style="color:#64748b;font-size:14px;margin:0 0 24px;line-height:1.6;font-weight:300;">
              The IITBase team has reviewed and verified your profile.
              You're now discoverable by recruiters hiring exclusively for IIT graduates.
            </p>
            <div style="background:#f0fdf4;border:1px solid #bbf7d0;border-radius:10px;
                        padding:16px 18px;margin-bottom:24px;">
              <p style="margin:0;font-size:13px;color:#166534;font-weight:400;line-height:1.6;">
                ✓ &nbsp;<strong>Verified IIT profile</strong> — your badge is now active.<br>
                ✓ &nbsp;Recruiters can view your profile and resume.<br>
                ✓ &nbsp;You can receive job invites directly from companies.
              </p>
            </div>
            <table cellpadding="0" cellspacing="0" width="100%%" style="margin-bottom:24px;">
              %s
              %s
              %s
            </table>
            %s
            %s
            <p style="color:#94a3b8;font-size:13px;margin:8px 0 0;line-height:1.6;">
              A strong profile gets 3× more recruiter views. Keep it updated.
            </p>
            """.formatted(
                name,
                s.featureRow("📄", "Upload your resume", "Recruiters download resumes before reaching out — make yours count."),
                s.featureRow("💼", "Add work experience", "Detailed experience helps recruiters match you to the right roles."),
                s.featureRow("🔔", "Turn on job alerts", "Get notified when roles matching your preferences are posted."),
                s.ctaButton(s.baseUrl + "/profile", "View my profile →"),
                s.divider()
        );
        return s.shell(content);
    }

    public String profileVerifiedText(String name) {
        return "Congratulations " + name + "! Your IITBase profile has been verified. "
                + "Recruiters can now discover you on the platform.";
    }
}