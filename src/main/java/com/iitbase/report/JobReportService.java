package com.iitbase.report;

import com.iitbase.job.Job;
import com.iitbase.job.JobService;
import com.iitbase.job.JobStatus;
import com.iitbase.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobReportService {

    private final JobReportRepository reportRepository;
    private final JobService jobService;

    @Value("${app.report-threshold:3}")
    private int reportThreshold;

    @Transactional
    public void reportJob(Long jobId, ReportReason reason, String comment, User user) {
        Job job = jobService.findById(jobId);

        JobReport report = JobReport.builder()
                .jobId(jobId)
                .reason(reason)
                .comment(comment)
                .reportedBy(user.getId())
                .build();

        reportRepository.save(report);

        long reportCount = reportRepository.countByJobId(jobId);
        log.info("Job {} reported. Total reports: {}", jobId, reportCount);

        if (reportCount >= reportThreshold && job.getStatus() == JobStatus.APPROVED) {
            job.setStatus(JobStatus.UNDER_REVIEW);
            jobService.save(job);
            log.warn("Job {} moved to UNDER_REVIEW due to {} reports", jobId, reportCount);
        }
    }
}