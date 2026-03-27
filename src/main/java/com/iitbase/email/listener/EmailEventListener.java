package com.iitbase.email.listener;

import com.iitbase.email.EmailService;
import com.iitbase.email.event.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Single listener for all email events.
 *
 * Key design decisions:
 *
 * 1. @TransactionalEventListener(phase = AFTER_COMMIT)
 *    Fires ONLY after the DB transaction commits successfully.
 *    If the service method rolls back (e.g. duplicate email, constraint violation),
 *    no email is sent. This prevents "ghost emails" for failed operations.
 *
 * 2. @Async
 *    Runs on a separate thread from the HTTP request.
 *    The API response returns immediately — email delivery doesn't block it.
 *    Requires @EnableAsync on a @Configuration class.
 *
 * 3. Exception handling
 *    Catches and logs all delivery failures. A failed email never
 *    propagates back to the caller or causes a transaction rollback.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmailEventListener {

    private final EmailService emailService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRecruiterTeamInvite(RecruiterTeamInviteEvent event) {
        try {
            emailService.sendRecruiterTeamInvite(
                    event.getToEmail(), event.getCompanyName(),
                    event.getInvitedByName(), event.getToken());
            log.info("Team invite email sent to: {}", event.getToEmail());
        } catch (Exception ex) {
            log.error("Failed to send team invite to {}: {}",
                    event.getToEmail(), ex.getMessage());
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRecruiterInviteAccepted(RecruiterInviteAcceptedEvent event) {
        try {
            emailService.sendRecruiterInviteAccepted(
                    event.getAdminEmail(), event.getAdminName(),
                    event.getNewMemberName(), event.getCompanyName());
            log.info("Invite accepted email sent to admin: {}", event.getAdminEmail());
        } catch (Exception ex) {
            log.error("Failed to send invite-accepted to {}: {}",
                    event.getAdminEmail(), ex.getMessage());
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onJoinRequestReceived(JoinRequestReceivedEvent event) {
        try {
            emailService.sendJoinRequestReceivedToAdmin(
                    event.getAdminEmail(), event.getAdminName(),
                    event.getRequesterEmail(), event.getCompanyName(),
                    event.getMessage());
            log.info("Join request email sent to admin: {}", event.getAdminEmail());
        } catch (Exception ex) {
            log.error("Failed to send join-request notification to {}: {}",
                    event.getAdminEmail(), ex.getMessage());
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onJoinRequestApproved(JoinRequestApprovedEvent event) {
        try {
            emailService.sendJoinRequestApproved(
                    event.getRequesterEmail(), event.getCompanyName(),
                    event.getApprovedByName());
            log.info("Join approved email sent to: {}", event.getRequesterEmail());
        } catch (Exception ex) {
            log.error("Failed to send join-approved to {}: {}",
                    event.getRequesterEmail(), ex.getMessage());
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onJoinRequestRejected(JoinRequestRejectedEvent event) {
        try {
            emailService.sendJoinRequestRejected(
                    event.getRequesterEmail(), event.getCompanyName(),
                    event.getRejectionReason());
            log.info("Join rejected email sent to: {}", event.getRequesterEmail());
        } catch (Exception ex) {
            log.error("Failed to send join-rejected to {}: {}",
                    event.getRequesterEmail(), ex.getMessage());
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onStaffInvite(StaffInviteEvent event) {
        try {
            emailService.sendStaffInvite(
                    event.getToEmail(), event.getInvitedByEmail(), event.getToken());
            log.info("Staff invite email sent to: {}", event.getToEmail());
        } catch (Exception ex) {
            log.error("Failed to send staff invite to {}: {}",
                    event.getToEmail(), ex.getMessage());
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onStaffInviteAccepted(StaffInviteAcceptedEvent event) {
        try {
            emailService.sendStaffInviteAccepted(
                    event.getSenderEmail(), event.getAcceptedByEmail());
            log.info("Staff invite accepted email sent to: {}", event.getSenderEmail());
        } catch (Exception ex) {
            log.error("Failed to send staff-invite-accepted to {}: {}",
                    event.getSenderEmail(), ex.getMessage());
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onJobseekerProfileVerified(JobseekerProfileVerifiedEvent event) {
        try {
            emailService.sendJobseekerProfileVerified(event.getEmail(), event.getFullName());
            log.info("Profile verified email sent to jobseeker: {}", event.getEmail());
        } catch (Exception ex) {
            log.error("Failed to send profile-verified email to {}: {}",
                    event.getEmail(), ex.getMessage());
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRecruiterCompanyVerified(RecruiterCompanyVerifiedEvent event) {
        try {
            emailService.sendRecruiterCompanyVerified(
                    event.getRecruiterEmail(),
                    event.getRecruiterName(),
                    event.getCompanyName());
            log.info("Company verified email sent to recruiter: {}", event.getRecruiterEmail());
        } catch (Exception ex) {
            log.error("Failed to send company-verified email to {}: {}",
                    event.getRecruiterEmail(), ex.getMessage());
        }
    }
}