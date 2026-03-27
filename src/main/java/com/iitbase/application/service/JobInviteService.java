package com.iitbase.application.service;

import com.iitbase.application.dto.request.SendInviteRequest;
import com.iitbase.application.dto.response.JobInviteResponse;
import com.iitbase.application.entity.JobInvite;
import com.iitbase.application.enums.InviteStatus;
import com.iitbase.application.repository.JobInviteRepository;
import com.iitbase.notification.dto.event.JobInviteEvent;
import com.iitbase.recruiter.entity.RecruiterJob;
import com.iitbase.recruiter.repository.RecruiterJobRepository;
import com.iitbase.recruiter.repository.RecruiterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class JobInviteService {

    private final JobInviteRepository       jobInviteRepository;
    private final RecruiterJobRepository    recruiterJobRepository;
    private final RecruiterRepository       recruiterRepository;
    private final ApplicationEventPublisher eventPublisher;

    // ── Recruiter sends invite ───────────────────────────────────────────────

    public JobInviteResponse sendInvite(Long userId, SendInviteRequest request) {
        Long recruiterId = resolveRecruiterId(userId);

        RecruiterJob job = recruiterJobRepository
                .findById(request.getRecruiterJobId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Job not found: " + request.getRecruiterJobId()
                ));

        // Guard: recruiter must own the job
        if (!job.getRecruiter().getId().equals(recruiterId)) {
            throw new IllegalStateException(
                    "You can only invite candidates for your own job listings"
            );
        }

        // Guard: no duplicate invites
        if (jobInviteRepository.existsByRecruiterIdAndJobseekerIdAndRecruiterJobId(
                recruiterId, request.getJobseekerId(), request.getRecruiterJobId())) {
            throw new IllegalStateException(
                    "You have already invited this candidate for this job"
            );
        }

        JobInvite invite = JobInvite.builder()
                .recruiterId(recruiterId)
                .jobseekerId(request.getJobseekerId())
                .recruiterJobId(request.getRecruiterJobId())
                .jobTitle(job.getTitle())
                .companyName(job.getCompany().getName())
                .message(request.getMessage())
                .status(InviteStatus.PENDING)
                .build();

        JobInvite saved = jobInviteRepository.save(invite);

        // Publish event → NotificationService will catch it
        eventPublisher.publishEvent(new JobInviteEvent(
                this,
                saved.getId(),
                request.getJobseekerId(),
                recruiterId,
                request.getRecruiterJobId(),
                job.getTitle(),
                job.getCompany().getName(),
                request.getMessage()
        ));

        log.info("Invite sent: recruiterId={} jobseekerId={} jobId={}",
                recruiterId, request.getJobseekerId(), request.getRecruiterJobId());

        return JobInviteResponse.from(saved);
    }

    // ── Jobseeker views their invites ────────────────────────────────────────

    @Transactional(readOnly = true)
    public Map<String, Object> getMyInvites(Long jobseekerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<JobInvite> invitePage = jobInviteRepository
                .findByJobseekerIdOrderByCreatedAtDesc(jobseekerId, pageable);

        return Map.of(
                "invites",      invitePage.getContent().stream()
                        .map(JobInviteResponse::from).toList(),
                "currentPage",  invitePage.getNumber(),
                "totalItems",   invitePage.getTotalElements(),
                "totalPages",   invitePage.getTotalPages()
        );
    }

    // ── Jobseeker declines invite ────────────────────────────────────────────

    public JobInviteResponse declineInvite(Long jobseekerId, Long inviteId) {
        JobInvite invite = jobInviteRepository
                .findByIdAndJobseekerId(inviteId, jobseekerId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invite not found: " + inviteId
                ));

        if (invite.getStatus() != InviteStatus.PENDING) {
            throw new IllegalStateException(
                    "Only pending invites can be declined"
            );
        }

        invite.setStatus(InviteStatus.DECLINED);
        jobInviteRepository.save(invite);

        log.info("Invite declined: inviteId={} jobseekerId={}", inviteId, jobseekerId);
        return JobInviteResponse.from(invite);
    }

    // ── Mark invite as accepted when jobseeker applies ──────────────────────
    // Called from ApplicationService when jobseeker applies via invite

    public void markAccepted(Long inviteId, Long applicationId) {
        jobInviteRepository.findById(inviteId).ifPresent(invite -> {
            invite.setStatus(InviteStatus.ACCEPTED);
            invite.setApplicationId(applicationId);
            jobInviteRepository.save(invite);
            log.info("Invite accepted: inviteId={} applicationId={}",
                    inviteId, applicationId);
        });
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private Long resolveRecruiterId(Long userId) {
        return recruiterRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException(
                        "Recruiter profile not found for userId: " + userId
                ))
                .getId();
    }
}