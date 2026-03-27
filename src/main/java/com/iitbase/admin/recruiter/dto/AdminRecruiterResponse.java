package com.iitbase.admin.recruiter.dto;

import com.iitbase.recruiter.entity.Recruiter;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AdminRecruiterResponse {
    private Long id;
    private Long userId;
    private String userEmail;
    private Long companyId;
    private String companyName;
    private Boolean companyVerified;
    private String designation;
    private Boolean isAdmin;
    private Boolean isSuspended;    // derived from token invalidation state
    private LocalDateTime createdAt;

    public static AdminRecruiterResponse from(Recruiter r, String email,
                                              boolean suspended) {
        return AdminRecruiterResponse.builder()
                .id(r.getId())
                .userId(r.getUserId())
                .userEmail(email)
                .companyId(r.getCompany().getId())
                .companyName(r.getCompany().getName())
                .companyVerified(r.getCompany().getIsVerified())
                .designation(r.getDesignation())
                .isAdmin(r.getIsAdmin())
                .isSuspended(suspended)
                .createdAt(r.getCreatedAt())
                .build();
    }
}