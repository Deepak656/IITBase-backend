package com.iitbase.admin.report;

import com.iitbase.common.ApiResponse;
import com.iitbase.report.JobReport;
import com.iitbase.report.JobReportRepository;
import com.iitbase.report.JobReportStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AdminReportService {

    private final JobReportRepository jobReportRepository;

    @Transactional(readOnly = true)
    public Page<JobReport> getAllReports(int page, int size, JobReportStatus status) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        if (status != null) {
            return jobReportRepository.findByJobReportStatus(status, pageable);
        }
        return jobReportRepository.findAll(pageable);
    }

    public JobReport resolveReport(Long reportId, String resolution) {
        JobReport report = jobReportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Report not found: " + reportId));
        report.setJobReportStatus(JobReportStatus.JOB_REMOVED);
        report.setResolution(resolution);
        JobReport saved = jobReportRepository.save(report);
        log.info("Admin resolved report: id={}", reportId);
        return saved;
    }

    public JobReport dismissReport(Long reportId) {
        JobReport report = jobReportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Report not found: " + reportId));
        report.setJobReportStatus(JobReportStatus.IGNORE);
        JobReport saved = jobReportRepository.save(report);
        log.info("Admin dismissed report: id={}", reportId);
        return saved;
    }
}