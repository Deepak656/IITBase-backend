package com.iitbase.recruiter.entity;

import com.iitbase.common.BaseEntity;
import com.iitbase.recruiter.enums.JoinRequestStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "team_join_requests",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_join_request_user_company",
                columnNames = {"user_id", "company_id"}
        )
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TeamJoinRequest extends BaseEntity {

    // User requesting to join
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    // Optional message from the requester e.g. "I'm the new HR at Google"
    @Column(columnDefinition = "TEXT")
    private String message;

    // Work email they claim to have at this company
    @Column(name = "work_email")
    private String workEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private JoinRequestStatus status = JoinRequestStatus.PENDING;

    // Recruiter (admin) who reviewed this request
    @Column(name = "reviewed_by_recruiter_id")
    private Long reviewedByRecruiterId;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    // Rejection reason shown to the requester
    @Column(name = "rejection_reason")
    private String rejectionReason;
}