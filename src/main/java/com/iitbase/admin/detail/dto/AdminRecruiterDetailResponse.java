package com.iitbase.admin.detail.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class AdminRecruiterDetailResponse {

    private Long   id;
    private Long   userId;
    private String email;
    private String name;
    private String designation;
    private String role;
    private String workEmail;
    private String phone;
    private Boolean isSuspended;
    private LocalDateTime createdAt;

    // Company they belong to
    private CompanyInfo company;

    // Jobs they have posted
    private List<JobSummary> jobs;

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
        private LocalDateTime createdAt;
    }

    @Getter @Builder
    public static class JobSummary {
        private Long   id;
        private String title;
        private String roleTitle;
        private String status;
        private String applyType;
        private LocalDateTime createdAt;
    }
}