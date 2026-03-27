package com.iitbase.admin.staff.dto;

import com.iitbase.admin.staff.StaffInvite;
import com.iitbase.admin.staff.StaffInviteStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter @Builder
public class StaffInviteResponse {
    private Long id;
    private String email;
    private String invitedByEmail;
    private StaffInviteStatus status;
    private LocalDateTime expiresAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime createdAt;

    public static StaffInviteResponse from(StaffInvite invite) {
        return StaffInviteResponse.builder()
                .id(invite.getId())
                .email(invite.getEmail())
                .invitedByEmail(invite.getInvitedByEmail())
                .status(invite.getStatus())
                .expiresAt(invite.getExpiresAt())
                .acceptedAt(invite.getAcceptedAt())
                .createdAt(invite.getCreatedAt())
                .build();
    }
}