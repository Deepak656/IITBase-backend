package com.iitbase.email.template;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RecruiterEmailTemplate {

    private final EmailTemplateShell s;

    // ── Company verified ──────────────────────────────────────────────────

    public String companyVerifiedHtml(String name, String companyName) {
        String features = """
            <table role="presentation" cellpadding="0" cellspacing="0" width="100%%"
                   style="margin-bottom:24px;">
              %s
              %s
              %s
            </table>
            """.formatted(
                s.featureRow("📋", "Post your first job",
                        "Verified companies get priority placement in search results. Your first listing is free."),
                s.featureRow("👥", "Invite your hiring team",
                        "Add recruiters so your entire team can manage applications from one dashboard."),
                s.featureRow("🔍", "Browse IIT profiles",
                        "Search verified IIT graduates by branch, batch, and skills. Send direct invites.")
        );

        String content = s.eyebrow("Company verified")
                + s.heading(companyName + " is verified on IITBase")
                + s.subtext("Hi " + name + " — the IITBase team has reviewed and verified "
                + "<strong style=\"color:#94a3b8;\">" + companyName + "</strong>. "
                + "Your listings now carry a verified badge, which significantly improves "
                + "candidate trust and application rates.")
                + s.successBox(
                "<strong style=\"color:#a7f3d0;\">&#10003; Verified company badge</strong> on all your job listings.<br>"
                        + "&#10003; Higher visibility in candidate search results.<br>"
                        + "&#10003; Candidates can now apply with full confidence.")
                + features
                + s.ctaButton(s.baseUrl + "/recruiter/jobs/new", "Post a job \u2192")
                + s.divider()
                + s.note("Questions? Reply to this email — we respond within one business day.");

        return s.shell(content);
    }

    public String companyVerifiedText(String name, String companyName) {
        return """
            %s is verified on IITBase
            
            Hi %s — your company now carries a verified badge on all listings.
            This improves candidate trust and application rates.
            
            Next steps:
            1. Post your first job — https://iitbase.com/recruiter/jobs/new
            2. Invite your hiring team
            3. Browse IIT profiles
            
            Questions? Reply to this email.
            
            — The IITBase team
            https://iitbase.com
            """.formatted(companyName, name);
    }

    // ── Team invite (to recipient) ────────────────────────────────────────

    public String teamInviteHtml(String companyName, String invitedByName, String link) {
        String content = s.eyebrow("Team invite")
                + s.heading("You've been invited to join " + companyName)
                + s.subtext("<strong style=\"color:#94a3b8;\">" + invitedByName + "</strong> has invited you "
                + "to join <strong style=\"color:#94a3b8;\">" + companyName + "</strong> as a recruiter "
                + "on IITBase. Accept to start posting jobs and managing applications.")
                + s.infoBox("Invited as <strong style=\"color:#c7d2fe;\">Recruiter</strong> at "
                + "<strong style=\"color:#c7d2fe;\">" + companyName + "</strong>")
                + s.ctaButton(link, "Accept invite \u2192")
                + s.linkFallback(link)
                + s.divider()
                + s.note("This invite expires in <strong style=\"color:#94a3b8;\">7 days</strong>. "
                + "If you weren't expecting this, you can safely ignore it.");

        return s.shell(content);
    }

    public String teamInviteText(String companyName, String invitedByName, String link) {
        return """
            %s invited you to join %s on IITBase
            
            You've been invited as a Recruiter at %s.
            Accept here: %s
            
            This invite expires in 7 days.
            If you weren't expecting this, you can safely ignore it.
            
            — The IITBase team
            """.formatted(invitedByName, companyName, companyName, link);
    }

    // ── Invite accepted (to admin who sent it) ────────────────────────────

    public String inviteAcceptedHtml(String adminName, String newMemberName, String companyName) {
        String content = s.eyebrow("Team update")
                + s.heading(newMemberName + " joined your team")
                + s.subtext("Hi " + adminName + " — <strong style=\"color:#94a3b8;\">" + newMemberName
                + "</strong> has accepted your invite and is now a recruiter at "
                + "<strong style=\"color:#94a3b8;\">" + companyName + "</strong> on IITBase.")
                + s.ctaButton(s.baseUrl + "/recruiter/team", "View team \u2192")
                + s.divider()
                + s.note("You can manage team members and permissions from your team dashboard.");

        return s.shell(content);
    }

    public String inviteAcceptedText(String newMemberName, String companyName) {
        return """
            %s joined %s on IITBase
            
            They accepted your invite and are now a recruiter on your team.
            View team: https://iitbase.com/recruiter/team
            
            — The IITBase team
            """.formatted(newMemberName, companyName);
    }

    // ── Invite expired (to original recipient) ────────────────────────────

    public String inviteExpiredHtml(String companyName) {
        String content = s.eyebrow("Invite expired")
                + s.heading("Your invite to " + companyName + " has expired")
                + s.subtext("The invite link for <strong style=\"color:#94a3b8;\">" + companyName
                + "</strong> on IITBase is no longer valid. Invite links expire after 7 days.")
                + s.note("Reach out to your team admin and ask them to send a new invite.")
                + s.divider()
                + s.note("Need help? Email us at "
                + "<a href=\"mailto:hello@iitbase.com\" style=\"color:#6366f1;\">hello@iitbase.com</a>.");

        return s.shell(content);
    }

    public String inviteExpiredText(String companyName) {
        return """
            Your invite to join %s on IITBase has expired.
            
            Invite links expire after 7 days. Ask the team admin to resend it.
            
            Need help? Email hello@iitbase.com
            
            — The IITBase team
            """.formatted(companyName);
    }
}