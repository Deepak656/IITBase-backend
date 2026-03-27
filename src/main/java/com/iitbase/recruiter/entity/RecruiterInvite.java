package com.iitbase.recruiter.entity;

import com.iitbase.common.BaseEntity;
import com.iitbase.recruiter.enums.InviteStatus;
import com.iitbase.recruiter.enums.TeamMemberRole;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "recruiter_invites")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RecruiterInvite extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String token;           // UUID, sent in invite email link

    @Column(nullable = false)
    private String email;           // who was invited

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    // Recruiter who sent the invite — must be ADMIN
    @Column(name = "invited_by_recruiter_id", nullable = false)
    private Long invitedByRecruiterId;

    // Role the invitee will get on joining
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TeamMemberRole intendedRole = TeamMemberRole.MEMBER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private InviteStatus status = InviteStatus.PENDING;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;    // 7 days from creation

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

    // userId of person who accepted — set on acceptance
    @Column(name = "accepted_by_user_id")
    private Long acceptedByUserId;
}