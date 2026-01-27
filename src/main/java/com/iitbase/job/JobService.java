package com.iitbase.job;

import com.iitbase.job.dto.*;
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
public class JobService {

    private final JobRepository jobRepository;

    public Map<String, Object> getPublicJobs(JobFilterRequest filter) {

        Pageable pageable = PageRequest.of(
                filter.getPage(),
                filter.getSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        LocalDateTime postedAfter = filter.getPostedAfter() != null
                ? LocalDateTime.of(LocalDate.parse(filter.getPostedAfter()), LocalTime.MIN)
                : null;

        var spec = Specification
                .where(JobSpecifications.approved())
                .and(JobSpecifications.role(filter.getRole()))
                .and(JobSpecifications.experience(
                        filter.getMinExperience(),
                        filter.getMaxExperience()
                ))
                .and(JobSpecifications.location(filter.getLocation()))
                .and(JobSpecifications.techStack(filter.getTechStack()))
                .and(postedAfter == null ? null :
                        (root, query, cb) ->
                                cb.greaterThanOrEqualTo(root.get("createdAt"), postedAfter)
                );

        Page<Job> jobPage = jobRepository.findAll(spec, pageable);

        return Map.of(
                "jobs", jobPage.getContent()
                        .stream()
                        .map(this::toResponse)
                        .toList(),
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

        Page<Job> jobPage = jobRepository.findAll(spec, pageable);

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
    @Transactional
    public void submitJob(JobCreateRequest request, User user) {
        Job job = Job.builder()
                .title(request.getTitle())
                .company(request.getCompany())
                .location(request.getLocation())
                .applyUrl(request.getApplyUrl())
                .sourceUrl(request.getSourceUrl())
                .minExperience(request.getMinExperience())
                .maxExperience(request.getMaxExperience())
                .primaryRole(request.getPrimaryRole())
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

    private JobResponse toResponse(Job job) {
        return JobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .company(job.getCompany())
                .location(job.getLocation())
                .minExperience(job.getMinExperience())
                .maxExperience(job.getMaxExperience())
                .primaryRole(job.getPrimaryRole())
                .jobDescription(job.getJobDescription())
                .techStack(job.getTechStack())
                .tierOneReason(job.getTierOneReason())
                .createdAt(job.getCreatedAt().toLocalDate().toString())
                .build();
    }

    private JobResponse toDetailResponse(Job job) {
        JobResponse response = toResponse(job);
        response.setJobDescription(job.getJobDescription());
        response.setApplyUrl(job.getApplyUrl());
        response.setSourceUrl(job.getSourceUrl());
        response.setSkills(job.getSkills());
        return response;
    }

    public Job findById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));
    }
    public JobResponse getJobById(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));
        return toDetailResponse(job); }
    @Transactional
    public void save(Job job) {
        jobRepository.save(job);
    }

}