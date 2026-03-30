package com.iitbase.email;

import com.iitbase.email.otp.OtpPurpose;
import com.iitbase.email.template.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final EmailProvider          emailProvider;
    private final EmailToggleConfig      toggleConfig;

    // ── Templates ─────────────────────────────────────────────────────────
    private final OtpEmailTemplate         otpTemplate;
    private final JobseekerEmailTemplate   jobseekerTemplate;
    private final RecruiterEmailTemplate   recruiterTemplate;
    private final JoinRequestEmailTemplate joinRequestTemplate;
    private final StaffEmailTemplate       staffTemplate;

    @Value("${app.base-url:https://iitbase.com}")
    private String baseUrl;

    // ── OTP ───────────────────────────────────────────────────────────────

    public void sendOtp(String to, String otp, OtpPurpose purpose) {
        if (!toggleConfig.canSendOtp()) {
            log.info("[EMAIL SUPPRESSED - OTP] to={} purpose={} otp={}", to, purpose, otp);
            return;
        }
        deliver(to,
                otpTemplate.subject(otp, purpose),
                otpTemplate.html(otp, purpose),
                otpTemplate.text(otp, purpose));
    }

    // ── Jobseeker ─────────────────────────────────────────────────────────

    public void sendJobseekerProfileVerified(String to, String name) {
        deliver(to,
                "Your IITBase profile is verified and live",
                jobseekerTemplate.profileVerifiedHtml(name),
                jobseekerTemplate.profileVerifiedText(name));
    }

    // ── Recruiter ─────────────────────────────────────────────────────────

    public void sendRecruiterCompanyVerified(String to, String name, String companyName) {
        deliver(to,
                companyName + " is now verified on IITBase",
                recruiterTemplate.companyVerifiedHtml(name, companyName),
                recruiterTemplate.companyVerifiedText(name, companyName));
    }

    public void sendRecruiterTeamInvite(String to, String companyName,
                                        String invitedByName, String token) {
        String link = baseUrl + "/recruiter/invite?token=" + token;
        deliver(to,
                invitedByName + " invited you to join " + companyName + " on IITBase",
                recruiterTemplate.teamInviteHtml(companyName, invitedByName, link),
                recruiterTemplate.teamInviteText(companyName, invitedByName, link));
    }

    public void sendRecruiterInviteAccepted(String to, String adminName,
                                            String newMemberName, String companyName) {
        deliver(to,
                newMemberName + " has joined your team on IITBase",
                recruiterTemplate.inviteAcceptedHtml(adminName, newMemberName, companyName),
                recruiterTemplate.inviteAcceptedText(newMemberName, companyName));
    }

    public void sendRecruiterInviteExpired(String to, String companyName) {
        deliver(to,
                "Your invite to " + companyName + " has expired",
                recruiterTemplate.inviteExpiredHtml(companyName),
                recruiterTemplate.inviteExpiredText(companyName));
    }

    // ── Join request ──────────────────────────────────────────────────────

    public void sendJoinRequestReceivedToAdmin(String adminEmail, String adminName,
                                               String requesterEmail, String companyName,
                                               String message) {
        String reviewLink = baseUrl + "/recruiter/team";
        deliver(adminEmail,
                "New join request for " + companyName,
                joinRequestTemplate.receivedHtml(adminName, requesterEmail,
                        companyName, message, reviewLink),
                joinRequestTemplate.receivedText(requesterEmail, companyName, reviewLink));
    }

    public void sendJoinRequestApproved(String to, String companyName, String approvedByName) {
        String dashboardLink = baseUrl + "/recruiter/dashboard";
        deliver(to,
                "You've been approved to join " + companyName,
                joinRequestTemplate.approvedHtml(companyName, approvedByName, dashboardLink),
                joinRequestTemplate.approvedText(companyName, approvedByName));
    }

    public void sendJoinRequestRejected(String to, String companyName, String reason) {
        deliver(to,
                "Your join request for " + companyName,
                joinRequestTemplate.rejectedHtml(companyName, reason),
                joinRequestTemplate.rejectedText(companyName, reason));
    }

    // ── Staff ─────────────────────────────────────────────────────────────

    public void sendStaffInvite(String to, String invitedByEmail, String token) {
        String link = baseUrl + "/admin/accept-invite?token=" + token;
        deliver(to,
                "You've been invited to join IITBase as staff",
                staffTemplate.inviteHtml(invitedByEmail, link),
                staffTemplate.inviteText(invitedByEmail, link));
    }

    public void sendStaffInviteAccepted(String to, String acceptedByEmail) {
        String dashboardLink = baseUrl + "/admin";
        deliver(to,
                acceptedByEmail + " has joined IITBase as staff",
                staffTemplate.acceptedHtml(acceptedByEmail, dashboardLink),
                staffTemplate.acceptedText(acceptedByEmail));
    }

    // ── Private delivery ──────────────────────────────────────────────────

    private void deliver(String to, String subject, String html, String text) {
        if (!toggleConfig.canSendEvents()) {
            log.info("[EMAIL SUPPRESSED - EVENT] to={} subject={}", to, subject);
            return;
        }
        try {
            emailProvider.send(EmailMessage.builder()
                    .to(to)
                    .subject(subject)
                    .htmlBody(html)
                    .textBody(text)
                    .build());
        } catch (Exception e) {
            log.error("Email delivery failed to={} subject={}: {}", to, subject, e.getMessage());
            throw e;
        }
    }
}