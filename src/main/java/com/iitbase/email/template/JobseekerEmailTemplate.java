package com.iitbase.email.template;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JobseekerEmailTemplate {

    private final EmailTemplateShell s;

    // ── Profile verified ──────────────────────────────────────────────────

    public String profileVerifiedHtml(String name) {
        String features = """
            <table role="presentation" cellpadding="0" cellspacing="0" width="100%%"
                   style="margin-bottom:24px;">
              %s
              %s
              %s
            </table>
            """.formatted(
                s.featureRow("📄", "Upload your resume",
                        "Recruiters download resumes before reaching out. A sharp resume gets you shortlisted faster."),
                s.featureRow("💼", "Add work experience",
                        "Detailed experience helps our matching algorithm surface you for the right roles."),
                s.featureRow("🔔", "Turn on job alerts",
                        "Get notified the moment a role matching your preferences is posted.")
        );

        String content = s.eyebrow("Profile verified")
                + s.heading("Your profile is live, " + name)
                + s.subtext("The IITBase team has reviewed and verified your profile. "
                + "You're now discoverable by recruiters hiring exclusively from IIT campuses.")
                + s.successBox(
                "<strong style=\"color:#a7f3d0;\">&#10003; Verified IIT badge</strong> is now active on your profile.<br>"
                        + "&#10003; Recruiters can view your profile and download your resume.<br>"
                        + "&#10003; You can receive direct job invites from companies.")
                + features
                + s.ctaButton(s.baseUrl + "/profile", "Complete my profile \u2192")
                + s.divider()
                + s.note("A complete profile gets 3&times; more recruiter views. Takes less than 5 minutes.");

        return s.shell(content);
    }

    public String profileVerifiedText(String name) {
        return """
            Your IITBase profile is verified, %s
            
            You're now discoverable by recruiters hiring exclusively from IIT campuses.
            Your verified IIT badge is active.
            
            Next steps:
            1. Upload your resume — https://iitbase.com/profile
            2. Add work experience
            3. Turn on job alerts
            
            Complete your profile: https://iitbase.com/profile
            
            — The IITBase team
            https://iitbase.com
            """.formatted(name);
    }

    // ── Job invite (recruiter reached out directly) ───────────────────────

    public String jobInviteHtml(String name, String companyName, String jobTitle, String jobLink) {
        String content = s.eyebrow("Job invite")
                + s.heading(companyName + " wants to connect")
                + s.subtext("Hi " + name + " — a recruiter at <strong style=\"color:#94a3b8;\">"
                + companyName + "</strong> has sent you a direct invite for the role of "
                + "<strong style=\"color:#94a3b8;\">" + jobTitle + "</strong>.")
                + s.infoBox("Role: <strong style=\"color:#c7d2fe;\">" + jobTitle + "</strong> &nbsp;&middot;&nbsp; "
                + "Company: <strong style=\"color:#c7d2fe;\">" + companyName + "</strong>")
                + s.ctaButton(jobLink, "View invite \u2192")
                + s.divider()
                + s.note("You can decline this invite from your dashboard. "
                + "IITBase does not share your contact details without your consent.");

        return s.shell(content);
    }

    public String jobInviteText(String name, String companyName, String jobTitle, String jobLink) {
        return """
            Hi %s,
            
            A recruiter at %s has sent you a direct invite for: %s
            
            View the invite: %s
            
            You can decline from your dashboard. IITBase does not share your contact
            details without your consent.
            
            — The IITBase team
            """.formatted(name, companyName, jobTitle, jobLink);
    }

    // ── Application status: shortlisted ──────────────────────────────────

    public String shortlistedHtml(String name, String companyName, String jobTitle, String dashboardLink) {
        String content = s.eyebrow("Application update")
                + s.heading("You've been shortlisted")
                + s.subtext("Hi " + name + " — you've been shortlisted by "
                + "<strong style=\"color:#94a3b8;\">" + companyName + "</strong> "
                + "for the role of <strong style=\"color:#94a3b8;\">" + jobTitle + "</strong>. "
                + "The recruiter may reach out shortly.")
                + s.successBox(
                "<strong style=\"color:#a7f3d0;\">&#10003; Shortlisted</strong> — the recruiter has reviewed your profile.<br>"
                        + "&#10003; Keep your profile updated for the best impression.<br>"
                        + "&#10003; Check your dashboard for any messages.")
                + s.ctaButton(dashboardLink, "View application \u2192")
                + s.divider()
                + s.note("You'll receive further updates from the recruiter directly. "
                + "Good luck.");

        return s.shell(content);
    }

    public String shortlistedText(String name, String companyName, String jobTitle, String dashboardLink) {
        return """
            Hi %s,
            
            You've been shortlisted by %s for: %s
            
            The recruiter may reach out shortly. View your application: %s
            
            Good luck.
            
            — The IITBase team
            """.formatted(name, companyName, jobTitle, dashboardLink);
    }
}