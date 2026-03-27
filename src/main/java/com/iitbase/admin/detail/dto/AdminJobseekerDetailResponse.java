package com.iitbase.admin.detail.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AdminJobseekerDetailResponse {

    private Long    id;
    private Long    userId;
    private String  email;
    private String  fullName;
    private String  phone;
    private String  headline;
    private String  summary;
    private String  profilePhotoUrl;
    private String  resumeUrl;
    private String  resumeFileName;
    private String  linkedinUrl;
    private String  githubUrl;
    private String  portfolioUrl;
    private Double  yearsOfExperience;
    private Boolean isOnCareerBreak;
    private Integer profileCompletion;
    private Boolean isVerified;
    private LocalDateTime verifiedAt;
    private LocalDateTime createdAt;
}