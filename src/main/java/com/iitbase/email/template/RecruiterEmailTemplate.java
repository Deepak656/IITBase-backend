package com.iitbase.email.template;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RecruiterEmailTemplate {

    private final EmailTemplateShell s;

    // ── Company verified ──────────────────────────────────────────────────

    public String companyVerifiedHtml(String name, String companyName) {
        String content = """
            <h2 style="margin:0 0 6px;font-size:22px;font-weight:600;color:#0f172a;
                       font-family:Georgia,serif;letter-spacing:-0.3px;">
              %s is verified on IITBase
            </h2>
            <p style="color:#64748b;font-size:14px;margin:0 0 24px;line-height:1.6;font-weight:300;">
              Hi %s — the IITBase team has reviewed and verified
              <strong style="color:#0f172a;">%s</strong>.
              Your listings now carry a verified badge, which significantly increases
              candidate trust and application rates.
            </p>
            <div style="background:#f0fdf4;border:1px solid #bbf7d0;border-radius:10px;
                        padding:16px 18px;margin-bottom:24px;">
              <p style="margin:0;font-size:13px;color:#166534;font-weight:400;line-height:1.6;">
                ✓ &nbsp;<strong>Verified company badge</strong> on all your listings.<br>
                ✓ &nbsp;Higher visibility in candidate search results.<br>
                ✓ &nbsp;Candidates can now apply with confidence.
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
              Questions? Reply to this email — we're here to help you hire well.
            </p>
            """.formatted(
                companyName, name, companyName,
                s.featureRow("📋", "Post more jobs", "Verified companies get better placement in job search results."),
                s.featureRow("👥", "Invite your team", "Add more recruiters so your entire hiring team is on IITBase."),
                s.featureRow("📩", "Browse candidates", "Search verified IIT profiles and send direct invites."),
                s.ctaButton(s.baseUrl + "/recruiter/jobs/new", "Post a job →"),
                s.divider()
        );
        return s.shell(content);
    }

    public String companyVerifiedText(String name, String companyName) {
        return "Great news " + name + "! " + companyName
                + " has been verified on IITBase. Your listings now show a verified badge.";
    }

    // ── Team invite ───────────────────────────────────────────────────────

    public String teamInviteHtml(String companyName, String invitedByName, String link) {
        String content = """
            <h2 style="margin:0 0 6px;font-size:22px;font-weight:600;color:#0f172a;
                       font-family:Georgia,serif;letter-spacing:-0.3px;">
              You've been invited to join %s
            </h2>
            <p style="color:#64748b;font-size:14px;margin:0 0 24px;line-height:1.6;font-weight:300;">
              <strong style="color:#0f172a;">%s</strong> has invited you to join
              <strong style="color:#0f172a;">%s</strong> as a recruiter on IITBase.
              Accept the invite to start posting jobs and managing applications.
            </p>
            <div style="background:#f8fafc;border:1px solid #e2e8f0;border-radius:10px;
                        padding:16px 18px;margin-bottom:8px;">
              <p style="margin:0;font-size:13px;color:#475569;font-weight:300;">
                Invited as <strong style="color:#0f172a;">Recruiter</strong> at
                <strong style="color:#0f172a;">%s</strong>
              </p>
            </div>
            %s
            %s
            <p style="color:#94a3b8;font-size:12px;margin:16px 0 0;line-height:1.6;">
              This invite expires in <strong>7 days</strong>.
              If you weren't expecting this, you can safely ignore it.
            </p>
            """.formatted(
                companyName, invitedByName, companyName, companyName,
                s.ctaButton(link, "Accept invite →"),
                s.linkFallback(link)
        );
        return s.shell(content);
    }

    public String teamInviteText(String companyName, String invitedByName, String link) {
        return invitedByName + " invited you to join " + companyName
                + " on IITBase as a recruiter. Accept here: " + link;
    }

    // ── Invite accepted ───────────────────────────────────────────────────

    public String inviteAcceptedHtml(String adminName, String newMemberName, String companyName) {
        String content = """
            <h2 style="margin:0 0 6px;font-size:22px;font-weight:600;color:#0f172a;
                       font-family:Georgia,serif;letter-spacing:-0.3px;">
              %s joined your team
            </h2>
            <p style="color:#64748b;font-size:14px;margin:0 0 24px;line-height:1.6;font-weight:300;">
              Hi %s — <strong style="color:#0f172a;">%s</strong> has accepted your invite
              and is now a recruiter at <strong style="color:#0f172a;">%s</strong> on IITBase.
            </p>
            %s
            <p style="color:#94a3b8;font-size:13px;margin:16px 0 0;line-height:1.6;">
              You can manage team members and permissions from your team dashboard.
            </p>
            """.formatted(
                newMemberName, adminName, newMemberName, companyName,
                s.ctaButton(s.baseUrl + "/recruiter/team", "View team →")
        );
        return s.shell(content);
    }

    public String inviteAcceptedText(String newMemberName, String companyName) {
        return newMemberName + " accepted your invite and joined " + companyName + " on IITBase.";
    }

    // ── Invite expired ────────────────────────────────────────────────────

    public String inviteExpiredHtml(String companyName) {
        String content = """
            <h2 style="margin:0 0 6px;font-size:22px;font-weight:600;color:#0f172a;
                       font-family:Georgia,serif;letter-spacing:-0.3px;">
              Your invite to %s has expired
            </h2>
            <p style="color:#64748b;font-size:14px;margin:0 0 24px;line-height:1.6;font-weight:300;">
              The invite link for <strong style="color:#0f172a;">%s</strong>
              on IITBase is no longer valid — invite links expire after 7 days.
            </p>
            <p style="color:#64748b;font-size:14px;margin:0;line-height:1.6;font-weight:300;">
              Reach out to your team admin and ask them to send a new invite.
            </p>
            %s
            """.formatted(companyName, companyName, s.divider());
        return s.shell(content);
    }

    public String inviteExpiredText(String companyName) {
        return "Your invite to join " + companyName + " on IITBase has expired. Ask the admin to resend.";
    }
}