package com.iitbase.admin.jobseeker.dto;

import com.iitbase.jobseeker.model.Jobseeker;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AdminJobseekerResponse {

    private Long    id;
    private Long    userId;
    private String  email;
    private String  fullName;
    private String  headline;
    private String  phone;
    private String  profilePhotoUrl;
    private String  resumeUrl;
    private String  linkedinUrl;
    private Integer profileCompletion;
    private Boolean isVerified;
    private LocalDateTime verifiedAt;
    private LocalDateTime createdAt;

    public static AdminJobseekerResponse from(Jobseeker j) {
        return AdminJobseekerResponse.builder()
                .id(j.getId())
                .userId(j.getUserId())
                .email(j.getEmail())
                .fullName(j.getFullName())
                .headline(j.getHeadline())
                .phone(j.getPhone())
                .profilePhotoUrl(j.getProfilePhotoUrl())
                .resumeUrl(j.getResumeUrl())
                .linkedinUrl(j.getLinkedinUrl())
                .profileCompletion(j.getProfileCompletion())
                .isVerified(j.getIsVerified())
                .verifiedAt(j.getVerifiedAt())
                .createdAt(j.getCreatedAt())
                .build();
    }
}