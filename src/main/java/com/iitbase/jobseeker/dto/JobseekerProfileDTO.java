package com.iitbase.jobseeker.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobseekerProfileDTO {

    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private String headline;
    private String summary;
    private String profilePhotoUrl;
    private String resumeUrl;
    private String resumeFileName;
    private String linkedinUrl;
    private String githubUrl;
    private String portfolioUrl;
    private Double yearsOfExperience;
    private Boolean isOnCareerBreak;
    private Integer profileCompletion;

    // Nested sections
    private List<WorkExperienceDTO> workExperiences;
    private List<EducationDTO> educations;
    private List<SkillDTO> skills;
    private List<ProjectDTO> projects;
    private List<CertificationDTO> certifications;
    private JobPreferenceDTO jobPreference;
}