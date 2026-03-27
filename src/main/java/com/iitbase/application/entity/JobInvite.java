package com.iitbase.application.entity;

import com.iitbase.application.enums.InviteStatus;
import com.iitbase.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "job_invites",
        uniqueConstraints = {
                // One invite per recruiter per jobseeker per job
                @UniqueConstraint(
                        name = "uq_invite_recruiter_jobseeker_job",
                        columnNames = {"recruiter_id", "jobseeker_id", "recruiter_job_id"}
                )
        }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class JobInvite extends BaseEntity {

    // Cross-module — plain Long, no @ManyToOne
    @Column(nullable = false)
    private Long recruiterId;

    @Column(nullable = false)
    private Long jobseekerId;

    @Column(nullable = false)
    private Long recruiterJobId;

    // Denormalized for notification display
    @Column(nullable = false)
    private String jobTitle;

    @Column(nullable = false)
    private String companyName;

    // Optional personal message from recruiter
    @Column(columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private InviteStatus status = InviteStatus.PENDING;

    // Set when jobseeker applies via this invite
    private Long applicationId;
}