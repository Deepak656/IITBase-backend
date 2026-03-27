package com.iitbase.notification.listener;

import com.iitbase.notification.dto.event.NewApplicationEvent;
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
public class NewApplicationListener {

    private final NotificationService notificationService;

    @Async
    @EventListener
    public void onNewApplication(NewApplicationEvent event) {
        notificationService.create(
                event.getRecruiterId(),
                UserRole.RECRUITER,
                NotificationType.NEW_APPLICATION,
                "New application received",
                event.getJobseekerName() + " applied for " + event.getJobTitle(),
                Map.of(
                        "applicationId", event.getApplicationId(),
                        "jobId",         event.getJobId(),
                        "jobseekerId",   event.getJobseekerId(),
                        "jobTitle",      event.getJobTitle()
                )
        );
    }
}