package com.iitbase.recruiter.dto.response;

import com.iitbase.recruiter.entity.RecruiterInvite;
import com.iitbase.recruiter.enums.InviteStatus;
import com.iitbase.recruiter.enums.TeamMemberRole;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RecruiterInviteResponse {
    private Long id;
    private String email;
    private TeamMemberRole intendedRole;
    private InviteStatus status;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;

    public static RecruiterInviteResponse from(RecruiterInvite invite) {
        return RecruiterInviteResponse.builder()
                .id(invite.getId())
                .email(invite.getEmail())
                .intendedRole(invite.getIntendedRole())
                .status(invite.getStatus())
                .expiresAt(invite.getExpiresAt())
                .createdAt(invite.getCreatedAt())
                .build();
    }
}