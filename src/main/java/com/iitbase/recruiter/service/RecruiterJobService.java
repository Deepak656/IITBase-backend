package com.iitbase.recruiter.service;

import com.iitbase.recruiter.dto.request.PostRecruiterJobRequest;
import com.iitbase.recruiter.dto.request.UpdateRecruiterJobRequest;
import com.iitbase.recruiter.dto.response.RecruiterJobResponse;
import com.iitbase.recruiter.entity.Recruiter;
import com.iitbase.recruiter.entity.RecruiterJob;
import com.iitbase.recruiter.enums.JobApplyType;
import com.iitbase.recruiter.enums.RecruiterJobStatus;
import com.iitbase.recruiter.exception.RecruiterJobNotFoundException;
import com.iitbase.recruiter.exception.RecruiterNotFoundException;
import com.iitbase.recruiter.exception.UnauthorizedActionException;
import com.iitbase.recruiter.repository.RecruiterJobRepository;
import com.iitbase.recruiter.repository.RecruiterRepository;
import com.iitbase.recruiter.spec.RecruiterJobSpecifications;
import com.iitbase.community.enums.JobDomain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RecruiterJobService {

    private final RecruiterJobRepository recruiterJobRepository;
    private final RecruiterRepository recruiterRepository;

    public RecruiterJobResponse postJob(Long userId, PostRecruiterJobRequest request) {
        Recruiter recruiter = recruiterRepository.findByUserId(userId)
                .orElseThrow(() -> new RecruiterNotFoundException(userId));

        validateJobRequest(request);

        RecruiterJob job = RecruiterJob.builder()
                .recruiter(recruiter)
                .company(recruiter.getCompany())  // job belongs to recruiter's company
                .title(request.getTitle())
                .roleTitle(request.getRoleTitle())
                .jobDomain(request.getJobDomain())
                .techRole(request.getTechRole())
                .location(request.getLocation())
                .jobDescription(request.getJobDescription())
                .minExperience(request.getMinExperience())
                .maxExperience(request.getMaxExperience())
                .applyType(request.getApplyType())
                .applyUrl(request.getApplyUrl())
                .salaryMin(request.getSalaryMin())
                .salaryMax(request.getSalaryMax())
                .currency(request.getCurrency() != null ? request.getCurrency() : "INR")
                .techStack(request.getTechStack())
                .skills(request.getSkills())
                .status(RecruiterJobStatus.ACTIVE)
                .expiresAt(request.getExpiresAt())
                .build();

        RecruiterJob saved = recruiterJobRepository.save(job);
        log.info("RecruiterJob posted by userId={} jobId={} applyType={}",
                userId, saved.getId(), saved.getApplyType());
        return RecruiterJobResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public RecruiterJobResponse getById(Long jobId) {
        RecruiterJob job = recruiterJobRepository.findById(jobId)
                .orElseThrow(() -> new RecruiterJobNotFoundException(jobId));
        return RecruiterJobResponse.from(job);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getMyJobs(Long userId, int page, int size) {
        Recruiter recruiter = recruiterRepository.findByUserId(userId)
                .orElseThrow(() -> new RecruiterNotFoundException(userId));

        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        var spec = RecruiterJobSpecifications.byRecruiter(recruiter.getId());
        Page<RecruiterJob> jobPage = recruiterJobRepository.findAll(spec, pageable);

        return Map.of(
                "jobs", jobPage.getContent().stream()
                        .map(RecruiterJobResponse::from).toList(),
                "currentPage", jobPage.getNumber(),
                "totalItems", jobPage.getTotalElements(),
                "totalPages", jobPage.getTotalPages()
        );
    }

    public RecruiterJobResponse updateJobStatus(Long userId, Long jobId,
                                                RecruiterJobStatus newStatus) {

        RecruiterJob job = findAndVerifyOwnership(userId, jobId);

        // 🔒 Optional: enforce transitions
        validateStatusTransition(job.getStatus(), newStatus);

        job.setStatus(newStatus);

        log.info("RecruiterJob status updated jobId={} from={} to={} by userId={}",
                jobId, job.getStatus(), newStatus, userId);

        return RecruiterJobResponse.from(recruiterJobRepository.save(job));
    }
    private void validateStatusTransition(RecruiterJobStatus current,
                                          RecruiterJobStatus next) {

        if (current == RecruiterJobStatus.REMOVED) {
            throw new IllegalStateException("Removed jobs cannot be modified");
        }

        if (current == RecruiterJobStatus.CLOSED && next == RecruiterJobStatus.ACTIVE) {
            throw new IllegalStateException("Closed jobs cannot be reopened");
        }

        // You can expand this later
    }

    public RecruiterJobResponse updateJob(Long userId, Long jobId,
                                          UpdateRecruiterJobRequest request) {
        RecruiterJob job = findAndVerifyOwnership(userId, jobId);

        if (request.getTitle() != null)          job.setTitle(request.getTitle());
        if (request.getRoleTitle() != null)       job.setRoleTitle(request.getRoleTitle());
        if (request.getLocation() != null)        job.setLocation(request.getLocation());
        if (request.getJobDescription() != null)  job.setJobDescription(request.getJobDescription());
        if (request.getMinExperience() != null)   job.setMinExperience(request.getMinExperience());
        if (request.getMaxExperience() != null)   job.setMaxExperience(request.getMaxExperience());
        if (request.getApplyUrl() != null)        job.setApplyUrl(request.getApplyUrl());
        if (request.getSalaryMin() != null)       job.setSalaryMin(request.getSalaryMin());
        if (request.getSalaryMax() != null)       job.setSalaryMax(request.getSalaryMax());
        if (request.getCurrency() != null)        job.setCurrency(request.getCurrency());
        if (request.getTechStack() != null)       job.setTechStack(request.getTechStack());
        if (request.getSkills() != null)          job.setSkills(request.getSkills());
        if (request.getExpiresAt() != null)       job.setExpiresAt(request.getExpiresAt());

        log.info("RecruiterJob updated jobId={} by userId={}", jobId, userId);
        return RecruiterJobResponse.from(recruiterJobRepository.save(job));
    }

    public void removeJob(Long userId, Long jobId) {
        RecruiterJob job = findAndVerifyOwnership(userId, jobId);
        job.setStatus(RecruiterJobStatus.REMOVED);
        recruiterJobRepository.save(job);
        log.info("RecruiterJob removed jobId={} by userId={}", jobId, userId);
    }

    // Called by JobFeedService in job module
    @Transactional(readOnly = true)
    public Page<RecruiterJob> getActiveJobs(Specification<RecruiterJob> spec,
                                            Pageable pageable) {
        return recruiterJobRepository.findAll(spec, pageable);
    }

    // ---- private helpers ----

    private void validateJobRequest(PostRecruiterJobRequest request) {
        if (request.getJobDomain() == JobDomain.TECHNOLOGY
                && request.getTechRole() == null) {
            throw new IllegalArgumentException(
                    "techRole is required for TECHNOLOGY domain jobs"
            );
        }
        if (request.getJobDomain() != JobDomain.TECHNOLOGY
                && request.getTechRole() != null) {
            throw new IllegalArgumentException(
                    "techRole should only be set for TECHNOLOGY domain jobs"
            );
        }
        if (request.getApplyType() == JobApplyType.EXTERNAL
                && (request.getApplyUrl() == null
                || request.getApplyUrl().isBlank())) {
            throw new IllegalArgumentException(
                    "applyUrl is required for EXTERNAL jobs"
            );
        }
        if (request.getApplyType() == JobApplyType.INTERNAL
                && request.getApplyUrl() != null) {
            throw new IllegalArgumentException(
                    "applyUrl must not be set for INTERNAL jobs"
            );
        }
    }

    private RecruiterJob findAndVerifyOwnership(Long userId, Long jobId) {
        RecruiterJob job = recruiterJobRepository.findById(jobId)
                .orElseThrow(() -> new RecruiterJobNotFoundException(jobId));

        Recruiter recruiter = recruiterRepository.findByUserId(userId)
                .orElseThrow(() -> new RecruiterNotFoundException(userId));

        if (!job.getRecruiter().getId().equals(recruiter.getId())) {
            throw new UnauthorizedActionException(
                    "You can only modify your own job listings"
            );
        }
        return job;
    }
}