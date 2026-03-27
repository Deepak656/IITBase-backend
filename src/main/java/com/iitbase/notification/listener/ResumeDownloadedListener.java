package com.iitbase.notification.listener;

import com.iitbase.notification.dto.event.ResumeDownloadedEvent;
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
public class ResumeDownloadedListener {

    private final NotificationService notificationService;

    @Async
    @EventListener
    public void onResumeDownloaded(ResumeDownloadedEvent event) {
        notificationService.create(
                event.getJobseekerId(),
                UserRole.JOB_SEEKER,
                NotificationType.RESUME_DOWNLOADED,
                "Your resume was downloaded",
                "A recruiter at " + event.getCompanyName()
                        + " downloaded your resume",
                Map.of(
                        "recruiterId", event.getRecruiterId(),
                        "companyName", event.getCompanyName()
                )
        );
    }
}