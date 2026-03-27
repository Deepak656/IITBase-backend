package com.iitbase.report;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobReportRepository extends JpaRepository<JobReport, Long> {
    long countByJobId(Long jobId);
    Page<JobReport> findByJobReportStatus(JobReportStatus status, Pageable pageable);
}