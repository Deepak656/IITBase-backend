package com.iitbase.admin.detail.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class RecruiterJobDetailResponse {

    // Job fields
    private Long    id;
    private String  title;
    private String  roleTitle;
    private String  jobDomain;
    private String  techRole;
    private String  location;
    private String  jobDescription;
    private Integer minExperience;
    private Integer maxExperience;
    private String  applyType;
    private String  applyUrl;
    private Long    salaryMin;
    private Long    salaryMax;
    private String  currency;
    private String  status;
    private List<String> techStack;
    private List<String> skills;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    // Who posted it
    private RecruiterInfo recruiter;

    // Company
    private CompanyInfo company;

    @Getter @Builder
    public static class RecruiterInfo {
        private Long   id;
        private Long   userId;
        private String email;
        private String name;
        private String designation;
        private String workEmail;
        private String phone;
        private String role;             // ADMIN | MEMBER
    }

    @Getter @Builder
    public static class CompanyInfo {
        private Long    id;
        private String  name;
        private String  website;
        private String  industry;
        private String  size;
        private Boolean isVerified;
        private String  status;
        private String  emailDomain;
    }
}