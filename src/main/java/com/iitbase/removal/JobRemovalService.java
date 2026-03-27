package com.iitbase.removal;

import com.iitbase.community.service.CommunityJobService;
import com.iitbase.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobRemovalService {

    private final JobRemovalRepository removalRepository;
    private final CommunityJobService jobService;

    @Transactional
    public void requestRemoval(Long jobId, String requesterEmail, String reason, User user) {
        jobService.findById(jobId);

        JobRemovalRequest request = JobRemovalRequest.builder()
                .jobId(jobId)
                .requesterEmail(requesterEmail)
                .reason(reason)
                .status(RemovalStatus.PENDING)
                .requestedBy(user.getId())
                .build();

        removalRepository.save(request);
        log.info("Removal requested for job {} by {}", jobId, requesterEmail);
    }
}