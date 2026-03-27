package com.iitbase.admin.detail.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class AdminCompanyDetailResponse {

    private Long    id;
    private String  name;
    private String  website;
    private String  industry;
    private String  size;
    private String  logoUrl;
    private String  description;
    private Boolean isVerified;
    private String  status;
    private String  emailDomain;
    private LocalDateTime createdAt;

    // Who created the company
    private CreatorInfo createdBy;

    // All recruiters belonging to this company
    private List<RecruiterSummary> recruiters;

    @Getter @Builder
    public static class CreatorInfo {
        private Long   userId;
        private String email;
        private String name;
        private String designation;
        private String workEmail;
        private String phone;
    }

    @Getter @Builder
    public static class RecruiterSummary {
        private Long   id;
        private String email;
        private String name;
        private String designation;
        private String workEmail;
        private String phone;
        private String role;
    }
}