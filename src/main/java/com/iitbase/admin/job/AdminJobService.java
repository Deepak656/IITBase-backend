package com.iitbase.admin.job;

import com.iitbase.community.entity.CommunityJob;
import com.iitbase.community.repository.CommunityJobRepository;
import com.iitbase.community.enums.JobStatus;
import com.iitbase.community.dto.JobResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminJobService {

    private final CommunityJobRepository jobRepository;

    public List<JobResponse> getPendingJobs() {
        return jobRepository.findByStatusOrderByCreatedAtAsc(JobStatus.PENDING)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<JobResponse> getReportedJobs() {
        return jobRepository.findByStatusIn(Arrays.asList(JobStatus.UNDER_REVIEW))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void approveJob(Long id) {
        CommunityJob job = findJob(id);
        job.setStatus(JobStatus.APPROVED);
        jobRepository.save(job);
        log.info("Job {} approved", id);
    }

    @Transactional
    public void rejectJob(Long id) {
        CommunityJob job = findJob(id);
        job.setStatus(JobStatus.REJECTED);
        jobRepository.save(job);
        log.info("Job {} rejected", id);
    }

    @Transactional
    public void markExpired(Long id) {
        CommunityJob job = findJob(id);
        job.setStatus(JobStatus.EXPIRED);
        jobRepository.save(job);
        log.info("Job {} marked expired", id);
    }

    private CommunityJob findJob(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));
    }

    private JobResponse toResponse(CommunityJob job) {
        return JobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .company(job.getCompany())
                .location(job.getLocation())
                .jobDescription(job.getJobDescription())
                .applyUrl(job.getApplyUrl())
                .sourceUrl(job.getSourceUrl())
                .minExperience(job.getMinExperience())
                .maxExperience(job.getMaxExperience())
                .jobDomain(job.getJobDomain())       // replaces primaryRole
                .techRole(job.getTechRole())          // new
                .roleTitle(job.getRoleTitle())        // new
                .techStack(job.getTechStack())
                .skills(job.getSkills())
                .tierOneReason(job.getTierOneReason())
                .createdAt(job.getCreatedAt().toLocalDate().toString())
                .status(job.getStatus())
                .build();
    }
}