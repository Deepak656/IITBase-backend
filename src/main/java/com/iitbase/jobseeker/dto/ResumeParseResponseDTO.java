package com.iitbase.jobseeker.dto;

import lombok.*;

import java.util.List;

/**
 * Returned by POST /api/v1/profile/resume/parse
 *
 * Fields map 1:1 to existing profile DTOs so the frontend can directly
 * pre-fill each section without any transformation. Fields the LLM
 * couldn't confidently extract are left null — frontend treats null
 * as "not detected" and leaves those inputs empty.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeParseResponseDTO {

    private BasicInfo basicInfo;
    private List<WorkExperienceDTO> workExperiences;
    private List<EducationDTO> educations;
    private List<SkillDTO> skills;
    private List<ProjectDTO> projects;
    private List<CertificationDTO> certifications;
    private ParsedJobPreference jobPreference;

    /** Subset of JobseekerBasicInfoRequest — links + contact only */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BasicInfo {
        private String fullName;
        private String phone;
        private String headline;       // LLM infers from most recent role
        private String summary;
        private String linkedinUrl;
        private String githubUrl;
        private String portfolioUrl;
        private Double yearsOfExperience;
    }
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ParsedJobPreference {
        private String currentLocation;
        private String primaryRole;
    }
}