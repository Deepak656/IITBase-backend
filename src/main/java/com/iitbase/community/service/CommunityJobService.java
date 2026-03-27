package com.iitbase.community.service;

import com.iitbase.community.entity.CommunityJob;
import com.iitbase.community.repository.CommunityJobRepository;
import com.iitbase.community.spec.JobSpecifications;
import com.iitbase.community.enums.JobDomain;
import com.iitbase.community.enums.JobStatus;
import com.iitbase.community.dto.*;
import com.iitbase.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommunityJobService {

    private final CommunityJobRepository jobRepository;

    // Update getPublicJobs() spec
    public Map<String, Object> getPublicJobs(JobFilterRequest filter) {
        Pageable pageable = PageRequest.of(
                filter.getPage(),
                filter.getSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        LocalDateTime postedAfter = filter.getPostedAfter() != null
                ? LocalDateTime.of(
                LocalDate.parse(filter.getPostedAfter()), LocalTime.MIN)
                : null;

        var spec = Specification
                .where(JobSpecifications.approved())
                .and(JobSpecifications.domain(filter.getDomain()))
                .and(JobSpecifications.techRole(filter.getTechRole()))
                .and(JobSpecifications.experience(
                        filter.getMinExperience(),
                        filter.getMaxExperience()))
                .and(JobSpecifications.location(filter.getLocation()))
                .and(JobSpecifications.techStack(filter.getTechStack()))
                .and(JobSpecifications.postedAfter(postedAfter));

        Page<CommunityJob> jobPage = jobRepository.findAll(spec, pageable);

        return Map.of(
                "jobs", jobPage.getContent().stream()
                        .map(this::toResponse).toList(),
                "page", jobPage.getNumber(),
                "totalPages", jobPage.getTotalPages()
        );
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getMyJobs(Long userId, MyJobsFilterRequest filter) {
        Pageable pageable = PageRequest.of(
                filter.getPage(),
                filter.getSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        var spec = Specification
                .where(JobSpecifications.submittedBy(userId))
                .and(JobSpecifications.statuses(filter.getStatuses()));

        Page<CommunityJob> jobPage = jobRepository.findAll(spec, pageable);

        return Map.of(
                "jobs", jobPage.getContent()
                        .stream()
                        .map(JobResponse::from)
                        .toList(),
                "currentPage", jobPage.getNumber(),
                "totalItems", jobPage.getTotalElements(),
                "totalPages", jobPage.getTotalPages()
        );
    }
    @Transactional(readOnly = true)
    public MyJobsStatsResponse getMyJobsStats(Long userId) {
        return MyJobsStatsResponse.builder()
                .total(jobRepository.countBySubmittedBy(userId))
                .pending(jobRepository.countBySubmittedByAndStatus(userId, JobStatus.PENDING))
                .approved(jobRepository.countBySubmittedByAndStatus(userId, JobStatus.APPROVED))
                .rejected(jobRepository.countBySubmittedByAndStatus(userId, JobStatus.REJECTED))
                .underReview(jobRepository.countBySubmittedByAndStatus(userId, JobStatus.UNDER_REVIEW))
                .expired(jobRepository.countBySubmittedByAndStatus(userId, JobStatus.EXPIRED))
                .build();
    }
    // Update submitJob() builder
    @Transactional
    public void submitJob(JobCreateRequest request, User user) {

        // Validation — techRole required for TECHNOLOGY domain
        if (request.getJobDomain() == JobDomain.TECHNOLOGY && request.getTechRole() == null) {
            throw new IllegalArgumentException(
                    "techRole is required for TECHNOLOGY domain jobs"
            );
        }
        if (request.getJobDomain() != JobDomain.TECHNOLOGY && request.getTechRole() != null) {
            throw new IllegalArgumentException(
                    "techRole should only be set for TECHNOLOGY domain jobs"
            );
        }

        CommunityJob job = CommunityJob.builder()
                .title(request.getTitle())
                .company(request.getCompany())
                .location(request.getLocation())
                .applyUrl(request.getApplyUrl())
                .sourceUrl(request.getSourceUrl())
                .minExperience(request.getMinExperience())
                .maxExperience(request.getMaxExperience())
                .jobDomain(request.getJobDomain())
                .techRole(request.getTechRole())
                .roleTitle(request.getRoleTitle())
                .techStack(request.getTechStack())
                .skills(request.getSkills())
                .jobDescription(request.getJobDescription())
                .tierOneReason(request.getTierOneReason())
                .status(JobStatus.PENDING)
                .submittedBy(user.getId())
                .build();

        jobRepository.save(job);
        log.info("Job submitted by user {}: {}", user.getEmail(), job.getTitle());
    }
    private JobResponse toResponse(CommunityJob job) {
        return JobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .company(job.getCompany())
                .location(job.getLocation())
                .minExperience(job.getMinExperience())
                .maxExperience(job.getMaxExperience())
                .jobDomain(job.getJobDomain())
                .jobDescription(job.getJobDescription())
                .techStack(job.getTechStack())
                .tierOneReason(job.getTierOneReason())
                .createdAt(job.getCreatedAt().toLocalDate().toString())
                .build();
    }

    private JobResponse toDetailResponse(CommunityJob job) {
        JobResponse response = toResponse(job);
        response.setJobDescription(job.getJobDescription());
        response.setApplyUrl(job.getApplyUrl());
        response.setSourceUrl(job.getSourceUrl());
        response.setSkills(job.getSkills());
        return response;
    }

    public CommunityJob findById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));
    }
    public JobResponse getJobById(Long id) {
        CommunityJob job = jobRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));
        return toDetailResponse(job); }
    @Transactional
    public void save(CommunityJob job) {
        jobRepository.save(job);
    }
    // Add this method to existing JobService.java
    @Transactional(readOnly = true)
    public Page<CommunityJob> findAll(Specification<CommunityJob> spec, Pageable pageable) {
        return jobRepository.findAll(spec, pageable);
    }

}