package com.iitbase.admin.detail.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class CommunityJobDetailResponse {

    // Job fields
    private Long    id;
    private String  title;
    private String  company;       // free-text company name on community job
    private String  location;
    private String  jobDescription;
    private String  applyUrl;
    private String  sourceUrl;
    private Integer minExperience;
    private Integer maxExperience;
    private String  jobDomain;
    private String  techRole;
    private String  roleTitle;
    private String  tierOneReason;
    private String  status;
    private List<String> techStack;
    private List<String> skills;
    private LocalDateTime createdAt;

    // Poster context
    private PosterInfo poster;

    @Getter @Builder
    public static class PosterInfo {
        private Long   userId;
        private String email;
        private String role;             // JOB_SEEKER | ADMIN
        // Populated only if role = JOB_SEEKER
        private String fullName;
        private String phone;
        private String headline;
        private String linkedinUrl;
        private Integer profileCompletion;
        private Boolean isVerified;
    }
}