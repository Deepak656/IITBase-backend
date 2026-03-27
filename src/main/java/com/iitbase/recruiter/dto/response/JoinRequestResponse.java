package com.iitbase.recruiter.dto.response;

import com.iitbase.recruiter.entity.TeamJoinRequest;
import com.iitbase.recruiter.enums.JoinRequestStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class JoinRequestResponse {
    private Long id;
    private Long userId;
    private Long companyId;
    private String companyName;
    private String message;
    private String workEmail;
    private JoinRequestStatus status;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime reviewedAt;

    public static JoinRequestResponse from(TeamJoinRequest r) {
        return JoinRequestResponse.builder()
                .id(r.getId())
                .userId(r.getUserId())
                .companyId(r.getCompany().getId())
                .companyName(r.getCompany().getName())
                .message(r.getMessage())
                .workEmail(r.getWorkEmail())
                .status(r.getStatus())
                .rejectionReason(r.getRejectionReason())
                .createdAt(r.getCreatedAt())
                .reviewedAt(r.getReviewedAt())
                .build();
    }
}