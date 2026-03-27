package com.iitbase.application.service;

import com.iitbase.application.dto.request.ApplyRequest;
import com.iitbase.application.dto.request.UpdateApplicationStatusRequest;
import com.iitbase.application.dto.response.ApplicationDetailResponse;
import com.iitbase.application.dto.response.ApplicationResponse;
import com.iitbase.application.dto.response.ApplicationStatusHistoryResponse;
import com.iitbase.application.entity.Application;
import com.iitbase.application.entity.ApplicationStatusHistory;
import com.iitbase.application.enums.ApplicationStatus;
import com.iitbase.application.event.ApplicationStatusChangedEvent;
import com.iitbase.application.exception.ApplicationNotFoundException;
import com.iitbase.application.exception.DuplicateApplicationException;
import com.iitbase.application.exception.InvalidStatusTransitionException;
import com.iitbase.application.repository.ApplicationRepository;
import com.iitbase.application.repository.ApplicationStatusHistoryRepository;
import com.iitbase.application.statemachine.ApplicationStateMachine;
import com.iitbase.notification.dto.event.NewApplicationEvent;
import com.iitbase.recruiter.entity.RecruiterJob;
import com.iitbase.recruiter.enums.JobApplyType;
import com.iitbase.recruiter.repository.RecruiterJobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ApplicationService {

    private final ApplicationRepository            applicationRepository;
    private final ApplicationStatusHistoryRepository historyRepository;
    private final RecruiterJobRepository           recruiterJobRepository;
    private final ApplicationEventPublisher        eventPublisher;

    // ── Jobseeker: Apply ─────────────────────────────────────────────────────

    public ApplicationResponse apply(Long jobseekerId,
                                     String resumeUrl,
                                     ApplyRequest request) {
        // Guard: job must exist and be INTERNAL
        RecruiterJob job = recruiterJobRepository
                .findById(request.getRecruiterJobId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Job not found: " + request.getRecruiterJobId()
                ));

        if (job.getApplyType() != JobApplyType.INTERNAL) {
            throw new IllegalArgumentException(
                    "This job does not accept applications through IITBase"
            );
        }

        if (job.getStatus() != com.iitbase.recruiter.enums.RecruiterJobStatus.ACTIVE) {
            throw new IllegalArgumentException(
                    "This job is no longer accepting applications"
            );
        }

        // Guard: idempotent — no duplicate applications
        if (applicationRepository.existsByRecruiterJobIdAndJobseekerId(
                request.getRecruiterJobId(), jobseekerId)) {
            throw new DuplicateApplicationException(
                    request.getRecruiterJobId(), jobseekerId
            );
        }

        // Guard: resume must exist on profile
        if (resumeUrl == null || resumeUrl.isBlank()) {
            throw new IllegalStateException(
                    "Please upload your resume to your profile before applying"
            );
        }

        Application application = Application.builder()
                .recruiterJobId(request.getRecruiterJobId())
                .jobseekerId(jobseekerId)
                .resumeUrl(resumeUrl)          // snapshot at apply time
                .coverNote(request.getCoverNote())
                .status(ApplicationStatus.APPLIED)
                .jobTitle(job.getTitle())      // denormalized
                .companyName(job.getCompany().getName())  // denormalized
                .recruiterId(job.getRecruiter().getId())
                .build();

        Application saved = applicationRepository.save(application);
        eventPublisher.publishEvent(new NewApplicationEvent(
                this,
                saved.getId(),
                saved.getRecruiterId(),
                saved.getJobseekerId(),
                saved.getRecruiterJobId(),
                saved.getJobTitle(),
                "A candidate"   // TODO: resolve jobseeker name from profile
        ));
        // Record initial status in history
        recordHistory(saved, null, ApplicationStatus.APPLIED,
                jobseekerId, null);

        log.info("Application submitted: jobseekerId={} jobId={} applicationId={}",
                jobseekerId, request.getRecruiterJobId(), saved.getId());

        return ApplicationResponse.from(saved);
    }

    // ── Jobseeker: View my applications ─────────────────────────────────────

    @Transactional(readOnly = true)
    public Map<String, Object> getMyApplications(Long jobseekerId,
                                                 int page, int size) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Application> appPage =
                applicationRepository.findByJobseekerId(jobseekerId, pageable);

        return Map.of(
                "applications", appPage.getContent().stream()
                        .map(ApplicationResponse::from).toList(),
                "currentPage",  appPage.getNumber(),
                "totalItems",   appPage.getTotalElements(),
                "totalPages",   appPage.getTotalPages()
        );
    }

    // ── Jobseeker: View one application + history ────────────────────────────

    @Transactional(readOnly = true)
    public Map<String, Object> getMyApplicationDetail(Long jobseekerId,
                                                      Long applicationId) {
        Application app = applicationRepository
                .findByIdAndJobseekerId(applicationId, jobseekerId)
                .orElseThrow(() -> new ApplicationNotFoundException(applicationId));

        List<ApplicationStatusHistoryResponse> history =
                historyRepository
                        .findByApplication_IdOrderByCreatedAtAsc(applicationId)
                        .stream()
                        .map(ApplicationStatusHistoryResponse::from)
                        .toList();

        return Map.of(
                "application", ApplicationResponse.from(app),
                "history",     history
        );
    }

    // ── Jobseeker: Withdraw ──────────────────────────────────────────────────

    public ApplicationResponse withdraw(Long jobseekerId, Long applicationId) {
        Application app = applicationRepository
                .findByIdAndJobseekerId(applicationId, jobseekerId)
                .orElseThrow(() -> new ApplicationNotFoundException(applicationId));

        ApplicationStatus previous = app.getStatus();

        // State machine validates withdrawal is allowed from current status
        ApplicationStateMachine.validateWithdrawal(previous);

        app.setStatus(ApplicationStatus.WITHDRAWN);
        applicationRepository.save(app);

        recordHistory(app, previous, ApplicationStatus.WITHDRAWN,
                jobseekerId, "Withdrawn by applicant");

        publishStatusEvent(app, previous, ApplicationStatus.WITHDRAWN);

        log.info("Application withdrawn: applicationId={} jobseekerId={}",
                applicationId, jobseekerId);

        return ApplicationResponse.from(app);
    }

    // ── Recruiter: View applicants for a job ─────────────────────────────────

    @Transactional(readOnly = true)
    public Map<String, Object> getApplicantsForJob(Long recruiterId,
                                                   Long recruiterJobId,
                                                   int page, int size) {
        // Verify recruiter owns this job
        verifyRecruiterOwnsJob(recruiterId, recruiterJobId);

        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        // Recruiters don't see withdrawn applications
        Page<Application> appPage = applicationRepository
                .findByRecruiterJobIdAndStatusNot(
                        recruiterJobId,
                        ApplicationStatus.WITHDRAWN,
                        pageable
                );

        return Map.of(
                "applications", appPage.getContent().stream()
                        .map(ApplicationDetailResponse::from).toList(),
                "currentPage",  appPage.getNumber(),
                "totalItems",   appPage.getTotalElements(),
                "totalPages",   appPage.getTotalPages(),
                "totalApplicants",
                applicationRepository.countByRecruiterJobId(recruiterJobId)
        );
    }

    // ── Recruiter: View one application detail ───────────────────────────────

    @Transactional(readOnly = true)
    public Map<String, Object> getApplicationDetail(Long recruiterId,
                                                    Long applicationId) {
        Application app = applicationRepository
                .findByIdAndRecruiterId(applicationId, recruiterId)
                .orElseThrow(() -> new ApplicationNotFoundException(applicationId));

        List<ApplicationStatusHistoryResponse> history =
                historyRepository
                        .findByApplication_IdOrderByCreatedAtAsc(applicationId)
                        .stream()
                        .map(ApplicationStatusHistoryResponse::from)
                        .toList();

        return Map.of(
                "application", ApplicationDetailResponse.from(app),
                "history",     history
        );
    }

    // ── Recruiter: Move pipeline status ──────────────────────────────────────

    public ApplicationDetailResponse updateStatus(Long recruiterId,
                                                  Long applicationId,
                                                  UpdateApplicationStatusRequest request) {
        Application app = applicationRepository
                .findByIdAndRecruiterId(applicationId, recruiterId)
                .orElseThrow(() -> new ApplicationNotFoundException(applicationId));

        ApplicationStatus previous = app.getStatus();
        ApplicationStatus next     = request.getStatus();

        // Guard: jobseeker withdrawal is not a recruiter action
        if (next == ApplicationStatus.WITHDRAWN) {
            throw new InvalidStatusTransitionException(
                    previous, next,
                    "Recruiters cannot set application status to WITHDRAWN"
            );
        }

        // State machine validates transition is legal
        ApplicationStateMachine.validateRecruiterTransition(previous, next);

        app.setStatus(next);

        // Update recruiter notes if provided
        if (request.getNote() != null && !request.getNote().isBlank()) {
            app.setRecruiterNotes(request.getNote());
        }

        applicationRepository.save(app);

        recordHistory(app, previous, next, recruiterId, request.getNote());

        publishStatusEvent(app, previous, next);

        log.info("Application status updated: applicationId={} {}→{} by recruiterId={}",
                applicationId, previous, next, recruiterId);

        return ApplicationDetailResponse.from(app);
    }

    // ── Recruiter: Update private notes ──────────────────────────────────────

    public ApplicationDetailResponse updateNotes(Long recruiterId,
                                                 Long applicationId,
                                                 String notes) {
        Application app = applicationRepository
                .findByIdAndRecruiterId(applicationId, recruiterId)
                .orElseThrow(() -> new ApplicationNotFoundException(applicationId));

        app.setRecruiterNotes(notes);
        applicationRepository.save(app);

        log.info("Recruiter notes updated: applicationId={} recruiterId={}",
                applicationId, recruiterId);

        return ApplicationDetailResponse.from(app);
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private void recordHistory(Application app,
                               ApplicationStatus from,
                               ApplicationStatus to,
                               Long changedBy,
                               String note) {
        ApplicationStatusHistory history = ApplicationStatusHistory.builder()
                .application(app)
                .fromStatus(from)
                .toStatus(to)
                .changedBy(changedBy)
                .note(note)
                .build();
        historyRepository.save(history);
    }

    private void publishStatusEvent(Application app,
                                    ApplicationStatus previous,
                                    ApplicationStatus next) {
        eventPublisher.publishEvent(new ApplicationStatusChangedEvent(
                this,
                app.getId(),
                app.getJobseekerId(),
                app.getRecruiterId(),
                app.getRecruiterJobId(),
                app.getJobTitle(),
                previous,
                next
        ));
    }

    private void verifyRecruiterOwnsJob(Long recruiterId, Long recruiterJobId) {
        RecruiterJob job = recruiterJobRepository
                .findById(recruiterJobId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Job not found: " + recruiterJobId
                ));
        if (!job.getRecruiter().getId().equals(recruiterId)) {
            throw new com.iitbase.application.exception
                    .ApplicationNotFoundException(recruiterJobId);
        }
    }
}