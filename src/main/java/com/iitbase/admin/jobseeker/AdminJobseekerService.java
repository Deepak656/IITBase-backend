package com.iitbase.admin.jobseeker;

import com.iitbase.admin.jobseeker.dto.AdminJobseekerResponse;
import com.iitbase.email.event.JobseekerProfileVerifiedEvent;
import com.iitbase.exception.ResourceNotFoundException;
import com.iitbase.jobseeker.model.Jobseeker;
import com.iitbase.jobseeker.repository.JobseekerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AdminJobseekerService {

    private final JobseekerRepository    jobseekerRepository;
    private final ApplicationEventPublisher eventPublisher;

    // ── List all jobseekers ───────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<AdminJobseekerResponse> getAll(int page, int size,
                                               String search,
                                               Boolean verified) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Jobseeker> result;

        if (search != null && !search.isBlank()) {
            result = jobseekerRepository
                    .findByFullNameContainingIgnoreCase(search.trim(), pageable);
        } else if (verified != null) {
            result = jobseekerRepository.findByIsVerified(verified, pageable);
        } else {
            result = jobseekerRepository.findAllByOrderByCreatedAtDesc(pageable);
        }

        return result.map(AdminJobseekerResponse::from);
    }

    // ── Get single jobseeker ──────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public AdminJobseekerResponse getOne(Long id) {
        return AdminJobseekerResponse.from(findById(id));
    }

    // ── Verify profile ────────────────────────────────────────────────────────

    public AdminJobseekerResponse verify(Long id) {
        Jobseeker jobseeker = findById(id);

        if (Boolean.TRUE.equals(jobseeker.getIsVerified())) {
            throw new IllegalStateException("Profile is already verified");
        }

        // Must have a name before we verify — sanity guard
        if (jobseeker.getFullName() == null || jobseeker.getFullName().isBlank()) {
            throw new IllegalStateException(
                    "Cannot verify a profile with no name. Ask the jobseeker to complete their profile first.");
        }

        jobseeker.setIsVerified(true);
        jobseeker.setVerifiedAt(LocalDateTime.now());
        Jobseeker saved = jobseekerRepository.save(jobseeker);

        // Publish after DB write — listener fires AFTER_COMMIT
        eventPublisher.publishEvent(new JobseekerProfileVerifiedEvent(
                this,
                saved.getEmail(),
                saved.getFullName()
        ));

        log.info("Admin verified jobseeker profile: id={} email={}", id, saved.getEmail());
        return AdminJobseekerResponse.from(saved);
    }

    // ── Unverify profile ──────────────────────────────────────────────────────

    public AdminJobseekerResponse unverify(Long id) {
        Jobseeker jobseeker = findById(id);
        jobseeker.setIsVerified(false);
        jobseeker.setVerifiedAt(null);
        Jobseeker saved = jobseekerRepository.save(jobseeker);
        log.warn("Admin unverified jobseeker profile: id={} email={}", id, saved.getEmail());
        return AdminJobseekerResponse.from(saved);
    }

    // ── Private ───────────────────────────────────────────────────────────────

    private Jobseeker findById(Long id) {
        return jobseekerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Jobseeker not found: " + id));
    }
}