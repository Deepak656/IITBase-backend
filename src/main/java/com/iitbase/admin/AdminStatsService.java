package com.iitbase.admin;

import com.iitbase.application.repository.ApplicationRepository;
import com.iitbase.application.repository.JobInviteRepository;
import com.iitbase.community.repository.CommunityJobRepository;
import com.iitbase.community.enums.JobStatus;
import com.iitbase.notification.repository.NotificationRepository;
import com.iitbase.recruiter.repository.CompanyRepository;
import com.iitbase.recruiter.repository.RecruiterJobRepository;
import com.iitbase.recruiter.enums.RecruiterJobStatus;
import com.iitbase.user.UserRepository;
import com.iitbase.user.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminStatsService {

    private final UserRepository          userRepository;
    private final CompanyRepository       companyRepository;
    private final CommunityJobRepository  jobRepository;
    private final RecruiterJobRepository  recruiterJobRepository;
    private final ApplicationRepository   applicationRepository;
    private final NotificationRepository  notificationRepository;
    private final JobInviteRepository     jobInviteRepository;

    public Map<String, Object> getDashboardStats() {
        return Map.of(
                "users",         getUserStats(),
                "companies",     getCompanyStats(),
                "jobs",          getJobStats(),
                "applications",  getApplicationStats(),
                "notifications", getNotificationStats()
        );
    }

    private Map<String, Object> getUserStats() {
        return Map.of(
                "total",      userRepository.count(),
                "jobSeekers", userRepository.countByRole(UserRole.JOB_SEEKER),
                "recruiters", userRepository.countByRole(UserRole.RECRUITER),
                "admins",     userRepository.countByRole(UserRole.ADMIN)
        );
    }

    private Map<String, Object> getCompanyStats() {
        return Map.of(
                "total",    companyRepository.count(),
                "verified", companyRepository.countByIsVerified(true),
                "pending",  companyRepository.countByIsVerified(false)
        );
    }

    private Map<String, Object> getJobStats() {
        return Map.of(
                "community", Map.of(
                        "total",      jobRepository.count(),
                        "pending",    jobRepository.countByStatus(JobStatus.PENDING),
                        "approved",   jobRepository.countByStatus(JobStatus.APPROVED),
                        "rejected",   jobRepository.countByStatus(JobStatus.REJECTED),
                        "underReview",jobRepository.countByStatus(JobStatus.UNDER_REVIEW),
                        "expired",    jobRepository.countByStatus(JobStatus.EXPIRED)
                ),
                "recruiter", Map.of(
                        "total",  recruiterJobRepository.count(),
                        "active", recruiterJobRepository.countByStatus(RecruiterJobStatus.ACTIVE),
                        "closed", recruiterJobRepository.countByStatus(RecruiterJobStatus.CLOSED),
                        "removed",recruiterJobRepository.countByStatus(RecruiterJobStatus.REMOVED)
                )
        );
    }

    private Map<String, Object> getApplicationStats() {
        return Map.of(
                "total",       applicationRepository.count(),
                "invitesSent", jobInviteRepository.count()
        );
    }

    private Map<String, Object> getNotificationStats() {
        return Map.of(
                "total",  notificationRepository.count(),
                "unread", notificationRepository.countByIsReadFalse()
        );
    }
}