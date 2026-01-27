package com.iitbase.report;

import com.iitbase.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "job_reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobReport extends BaseEntity {

    @Column(nullable = false)
    private Long jobId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportReason reason;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(nullable = false)
    private Long reportedBy;

    private String reporterEmail;
    @Builder.Default
    private JobReportStatus jobReportStatus = JobReportStatus.NEW;
}