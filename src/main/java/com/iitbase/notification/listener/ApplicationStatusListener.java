package com.iitbase.notification.listener;

import com.iitbase.application.enums.ApplicationStatus;
import com.iitbase.application.event.ApplicationStatusChangedEvent;
import com.iitbase.notification.enums.NotificationType;
import com.iitbase.notification.service.NotificationService;
import com.iitbase.user.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationStatusListener {

    private final NotificationService notificationService;

    @Async   // don't block the recruiter's HTTP thread
    @EventListener
    public void onStatusChanged(ApplicationStatusChangedEvent event) {

        // ── Notify jobseeker of pipeline movement ────────────────────────────
        NotificationType type    = resolveTypeForJobseeker(event.getNewStatus());
        String           title   = resolveTitleForJobseeker(event.getNewStatus());

        if (type != null) {
            String message = resolveMessageForJobseeker(
                    event.getNewStatus(), event.getJobTitle());

            notificationService.create(
                    event.getJobseekerId(),
                    UserRole.JOB_SEEKER,
                    type,
                    title,
                    message,
                    Map.of(
                            "applicationId", event.getApplicationId(),
                            "jobId",         event.getJobId(),
                            "jobTitle",      event.getJobTitle()
                    )
            );
        }
    }

    private NotificationType resolveTypeForJobseeker(ApplicationStatus status) {
        return switch (status) {
            case SCREENING, INTERVIEW -> NotificationType.APPLICATION_SHORTLISTED;
            case OFFER                -> NotificationType.APPLICATION_OFFER;
            case HIRED                -> NotificationType.APPLICATION_HIRED;
            case REJECTED             -> NotificationType.APPLICATION_REJECTED;
            default                   -> null;   // APPLIED, WITHDRAWN — no notification
        };
    }

    private String resolveTitleForJobseeker(ApplicationStatus status) {
        return switch (status) {
            case SCREENING  -> "You've been shortlisted";
            case INTERVIEW  -> "Interview scheduled";
            case OFFER      -> "You have an offer!";
            case HIRED      -> "Congratulations!";
            case REJECTED   -> "Application update";
            default         -> null;
        };
    }

    private String resolveMessageForJobseeker(ApplicationStatus status,
                                              String jobTitle) {
        return switch (status) {
            case SCREENING -> "Your application for " + jobTitle
                    + " has moved to screening";
            case INTERVIEW -> "You've been selected for an interview for "
                    + jobTitle;
            case OFFER     -> "You have received an offer for " + jobTitle
                    + ". Check your application for details";
            case HIRED     -> "You have been hired for " + jobTitle
                    + ". Welcome aboard!";
            case REJECTED  -> "Your application for " + jobTitle
                    + " was not selected this time";
            default        -> null;
        };
    }
}