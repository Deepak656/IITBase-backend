package com.iitbase.application.entity;

import com.iitbase.application.enums.ApplicationStatus;
import com.iitbase.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "applications",
        uniqueConstraints = {
                // One application per jobseeker per job — enforced at DB level
                @UniqueConstraint(
                        name = "uq_application_job_jobseeker",
                        columnNames = {"recruiter_job_id", "jobseeker_id"}
                )
        }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Application extends BaseEntity {

    // Cross-module — stored as Long, no @ManyToOne to RecruiterJob
    @Column(name = "recruiter_job_id", nullable = false)
    private Long recruiterJobId;

    // Cross-module — stored as Long, no @ManyToOne to User
    @Column(nullable = false)
    private Long jobseekerId;

    // Snapshot of resume URL at time of apply
    // Jobseeker's profile resume may change — this preserves what was submitted
    @Column(nullable = false)
    private String resumeUrl;

    // Optional cover note from jobseeker
    @Column(columnDefinition = "TEXT")
    private String coverNote;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ApplicationStatus status = ApplicationStatus.APPLIED;

    // Private recruiter notes — not visible to jobseeker
    @Column(columnDefinition = "TEXT")
    private String recruiterNotes;

    // Denormalized for feed display — avoids cross-module join
    @Column(nullable = false)
    private String jobTitle;

    @Column(nullable = false)
    private String companyName;

    // FK to recruiter who owns this job — for ownership checks
    @Column(nullable = false)
    private Long recruiterId;
}