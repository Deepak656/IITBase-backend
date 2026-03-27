package com.iitbase.admin.removal;

import com.iitbase.community.entity.CommunityJob;
import com.iitbase.community.enums.JobStatus;
import com.iitbase.community.repository.CommunityJobRepository;
import com.iitbase.removal.JobRemovalRequest;
import com.iitbase.removal.JobRemovalRepository;
import com.iitbase.removal.RemovalStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AdminPostRemovalService {

    private final JobRemovalRepository          jobRemovalRepository;
    private final CommunityJobRepository        jobRepository;

    @Transactional(readOnly = true)
    public Page<JobRemovalRequest> getAllRequests(int page, int size,
                                                  RemovalStatus status) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        if (status != null) {
            return jobRemovalRepository.findByStatus(status, pageable);
        }
        return jobRemovalRepository.findAll(pageable);
    }

    // Approve removal — removes the job from public feed
    public JobRemovalRequest approveRemoval(Long requestId) {
        JobRemovalRequest request = findRequest(requestId);

        // Remove the job
        CommunityJob job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Job not found: " + request.getJobId()));
        job.setStatus(JobStatus.EXPIRED);
        jobRepository.save(job);

        request.setStatus(RemovalStatus.APPROVED);
        JobRemovalRequest saved = jobRemovalRepository.save(request);
        log.info("Admin approved removal request: id={} jobId={}",
                requestId, request.getJobId());
        return saved;
    }

    // Deny removal — keep job live
    public JobRemovalRequest denyRemoval(Long requestId) {
        JobRemovalRequest request = findRequest(requestId);
        request.setStatus(RemovalStatus.DENIED);
        JobRemovalRequest saved = jobRemovalRepository.save(request);
        log.info("Admin denied removal request: id={}", requestId);
        return saved;
    }

    private JobRemovalRequest findRequest(Long id) {
        return jobRemovalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Removal request not found: " + id));
    }
}