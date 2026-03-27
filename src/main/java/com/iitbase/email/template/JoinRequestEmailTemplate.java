package com.iitbase.email.template;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JoinRequestEmailTemplate {

    private final EmailTemplateShell s;

    // ── Received (to admin) ───────────────────────────────────────────────

    public String receivedHtml(String adminName, String requesterEmail,
                               String companyName, String message, String reviewLink) {
        String messageBlock = message != null && !message.isBlank()
                ? """
                  <div style="background:#f8fafc;border-left:3px solid #6366f1;
                              border-radius:0 8px 8px 0;padding:14px 16px;margin:16px 0;">
                    <p style="margin:0;font-size:13px;color:#475569;font-style:italic;
                               line-height:1.6;font-weight:300;">"%s"</p>
                  </div>
                  """.formatted(message)
                : "";

        String content = """
            <h2 style="margin:0 0 6px;font-size:22px;font-weight:600;color:#0f172a;
                       font-family:Georgia,serif;letter-spacing:-0.3px;">
              New join request for %s
            </h2>
            <p style="color:#64748b;font-size:14px;margin:0 0 16px;line-height:1.6;font-weight:300;">
              Hi %s — <strong style="color:#0f172a;">%s</strong> has requested to join
              <strong style="color:#0f172a;">%s</strong> as a recruiter on IITBase.
            </p>
            %s
            <p style="color:#64748b;font-size:14px;margin:0 0 8px;line-height:1.6;font-weight:300;">
              Review and approve or reject from your team dashboard.
            </p>
            %s
            <p style="color:#94a3b8;font-size:12px;margin:8px 0 0;line-height:1.6;">
              Only company admins can approve or reject join requests.
            </p>
            """.formatted(
                companyName, adminName, requesterEmail, companyName,
                messageBlock,
                s.ctaButton(reviewLink, "Review request →")
        );
        return s.shell(content);
    }

    public String receivedText(String requesterEmail, String companyName, String reviewLink) {
        return requesterEmail + " requested to join " + companyName
                + " on IITBase. Review here: " + reviewLink;
    }

    // ── Approved (to requester) ───────────────────────────────────────────

    public String approvedHtml(String companyName, String approvedByName, String dashboardLink) {
        String content = """
            <h2 style="margin:0 0 6px;font-size:22px;font-weight:600;color:#0f172a;
                       font-family:Georgia,serif;letter-spacing:-0.3px;">
              You've been approved to join %s
            </h2>
            <p style="color:#64748b;font-size:14px;margin:0 0 24px;line-height:1.6;font-weight:300;">
              <strong style="color:#0f172a;">%s</strong> has approved your request to join
              <strong style="color:#0f172a;">%s</strong> as a recruiter on IITBase.
              You now have access to post jobs and manage applications.
            </p>
            %s
            <p style="color:#94a3b8;font-size:13px;margin:16px 0 0;line-height:1.6;">
              Questions about your access? Contact your team admin directly.
            </p>
            """.formatted(
                companyName, approvedByName, companyName,
                s.ctaButton(dashboardLink, "Go to dashboard →")
        );
        return s.shell(content);
    }

    public String approvedText(String companyName, String approvedByName) {
        return "Your request to join " + companyName + " was approved by " + approvedByName + ".";
    }

    // ── Rejected (to requester) ───────────────────────────────────────────

    public String rejectedHtml(String companyName, String reason) {
        String reasonBlock = reason != null && !reason.isBlank()
                ? """
                  <div style="background:#fef2f2;border:1px solid #fecaca;border-radius:10px;
                              padding:14px 16px;margin:16px 0;">
                    <p style="margin:0;font-size:13px;color:#b91c1c;font-weight:300;line-height:1.6;">
                      <strong>Reason:</strong> %s
                    </p>
                  </div>
                  """.formatted(reason)
                : "";

        String content = """
            <h2 style="margin:0 0 6px;font-size:22px;font-weight:600;color:#0f172a;
                       font-family:Georgia,serif;letter-spacing:-0.3px;">
              Your join request for %s
            </h2>
            <p style="color:#64748b;font-size:14px;margin:0 0 16px;line-height:1.6;font-weight:300;">
              Your request to join <strong style="color:#0f172a;">%s</strong>
              on IITBase was not approved at this time.
            </p>
            %s
            <p style="color:#64748b;font-size:14px;margin:16px 0 0;line-height:1.6;font-weight:300;">
              If you believe this was a mistake, contact the company admin or reach out to
              <a href="mailto:hello@iitbase.com" style="color:#6366f1;">hello@iitbase.com</a>.
            </p>
            """.formatted(companyName, companyName, reasonBlock);
        return s.shell(content);
    }

    public String rejectedText(String companyName, String reason) {
        return "Your request to join " + companyName + " was not approved."
                + (reason != null ? " Reason: " + reason : "");
    }
}