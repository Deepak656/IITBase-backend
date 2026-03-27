package com.iitbase.notification.listener;

import com.iitbase.notification.dto.event.JobInviteEvent;
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
public class JobInviteListener {

    private final NotificationService notificationService;

    @Async
    @EventListener
    public void onJobInvite(JobInviteEvent event) {
        notificationService.create(
                event.getJobseekerId(),
                UserRole.JOB_SEEKER,
                NotificationType.JOB_INVITE,
                "You've been invited to apply",
                event.getCompanyName() + " invited you to apply for "
                        + event.getJobTitle(),
                Map.of(
                        "inviteId",    event.getInviteId(),
                        "jobId",       event.getJobId(),
                        "jobTitle",    event.getJobTitle(),
                        "companyName", event.getCompanyName(),
                        "message",     event.getMessage() != null
                                ? event.getMessage() : ""
                )
        );
    }
}