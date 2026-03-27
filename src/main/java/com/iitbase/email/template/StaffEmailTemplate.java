package com.iitbase.email.template;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StaffEmailTemplate {

    private final EmailTemplateShell s;

    // ── Invite (to recipient) ─────────────────────────────────────────────

    public String inviteHtml(String invitedByEmail, String link) {
        String content = """
            <h2 style="margin:0 0 6px;font-size:22px;font-weight:600;color:#0f172a;
                       font-family:Georgia,serif;letter-spacing:-0.3px;">
              You've been invited to join IITBase as staff
            </h2>
            <p style="color:#64748b;font-size:14px;margin:0 0 24px;line-height:1.6;font-weight:300;">
              <strong style="color:#0f172a;">%s</strong> has invited you to join the
              IITBase operations team. As a staff member, you'll have access to the admin
              dashboard to manage companies, recruiters, and job moderation.
            </p>
            <div style="background:#f0f0ff;border:1px solid #c7d2fe;border-radius:10px;
                        padding:16px 18px;margin-bottom:8px;">
              <p style="margin:0;font-size:13px;color:#3730a3;font-weight:400;line-height:1.5;">
                🔐 <strong>Staff access</strong> — admin dashboard, moderation tools,
                company verification, recruiter management.
              </p>
            </div>
            %s
            %s
            <p style="color:#94a3b8;font-size:12px;margin:0;line-height:1.6;">
              This invite expires in <strong>7 days</strong>.
              If you weren't expecting this, you can safely ignore it.
            </p>
            """.formatted(
                invitedByEmail,
                s.ctaButton(link, "Accept staff invite →"),
                s.linkFallback(link)
        );
        return s.shell(content);
    }

    public String inviteText(String invitedByEmail, String link) {
        return invitedByEmail + " invited you to join IITBase as staff. Accept here: " + link;
    }

    // ── Accepted (to sender) ──────────────────────────────────────────────

    public String acceptedHtml(String acceptedByEmail, String dashboardLink) {
        String content = """
            <h2 style="margin:0 0 6px;font-size:22px;font-weight:600;color:#0f172a;
                       font-family:Georgia,serif;letter-spacing:-0.3px;">
              %s joined IITBase as staff
            </h2>
            <p style="color:#64748b;font-size:14px;margin:0 0 24px;line-height:1.6;font-weight:300;">
              <strong style="color:#0f172a;">%s</strong> has accepted your staff invite
              and now has access to the IITBase admin dashboard.
            </p>
            %s
            <p style="color:#94a3b8;font-size:13px;margin:16px 0 0;line-height:1.6;">
              You can manage staff access from the Staff section of the admin dashboard.
            </p>
            """.formatted(
                acceptedByEmail, acceptedByEmail,
                s.ctaButton(dashboardLink, "View admin dashboard →")
        );
        return s.shell(content);
    }

    public String acceptedText(String acceptedByEmail) {
        return acceptedByEmail + " accepted your staff invite and now has admin dashboard access.";
    }
}