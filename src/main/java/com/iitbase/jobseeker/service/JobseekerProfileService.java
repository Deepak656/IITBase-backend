package com.iitbase.jobseeker.service;

import com.iitbase.exception.ResourceNotFoundException;
import com.iitbase.jobseeker.dto.*;
import com.iitbase.jobseeker.model.*;
import com.iitbase.jobseeker.repository.*;
import com.iitbase.jobseeker.storage.R2StorageService;
import com.iitbase.user.User;
import com.iitbase.user.UserRepository;
import com.iitbase.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobseekerProfileService {

    private final JobseekerRepository jobseekerRepository;
    private final WorkExperienceRepository workExperienceRepository;
    private final EducationRepository educationRepository;
    private final SkillRepository skillRepository;
    private final ProjectRepository projectRepository;
    private final CertificationRepository certificationRepository;
    private final JobPreferenceService jobPreferenceService;  // delegate, don't duplicate
    private final R2StorageService r2StorageService;
    private final UserService userService;
    private final UserRepository userRepository;
    // ─────────────────────────────────────────────
    // Bootstrap — call this after signup/login
    // ─────────────────────────────────────────────

    @Transactional
    public Jobseeker getOrCreateProfile(String email) {
        User user = userService.findByEmail(email);
        return jobseekerRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Jobseeker profile = Jobseeker.builder()
                            .userId(user.getId())
                            .email(user.getEmail())
                            .profileCompletion(0)
                            .isOnCareerBreak(false)
                            .build();
                    log.info("Created jobseeker profile for: {}", email);
                    return jobseekerRepository.save(profile);
                });
    }

    // ─────────────────────────────────────────────
    // Full profile — single response for the frontend
    // ─────────────────────────────────────────────

    @Transactional(readOnly = true)
    public JobseekerProfileDTO getFullProfile(String email) {
        Jobseeker jobseeker = resolveJobseeker(email);

        return JobseekerProfileDTO.builder()
                .id(jobseeker.getId())
                .email(jobseeker.getEmail())
                .fullName(jobseeker.getFullName())
                .phone(jobseeker.getPhone())
                .headline(jobseeker.getHeadline())
                .summary(jobseeker.getSummary())
                .profilePhotoUrl(jobseeker.getProfilePhotoUrl())
                .resumeUrl(jobseeker.getResumeUrl())
                .resumeFileName(jobseeker.getResumeFileName())
                .linkedinUrl(jobseeker.getLinkedinUrl())
                .githubUrl(jobseeker.getGithubUrl())
                .portfolioUrl(jobseeker.getPortfolioUrl())
                .yearsOfExperience(jobseeker.getYearsOfExperience())
                .isOnCareerBreak(jobseeker.getIsOnCareerBreak())
                .profileCompletion(jobseeker.getProfileCompletion())
                .workExperiences(
                        workExperienceRepository
                                .findByJobseekerIdOrderByDisplayOrderAscStartYearDesc(jobseeker.getId())
                                .stream().map(this::toWorkExpDTO).toList()
                )
                .educations(
                        educationRepository
                                .findByJobseekerIdOrderByDisplayOrderAscEndYearDesc(jobseeker.getId())
                                .stream().map(this::toEducationDTO).toList()
                )
                .skills(
                        skillRepository
                                .findByJobseekerIdOrderByDisplayOrderAsc(jobseeker.getId())
                                .stream().map(this::toSkillDTO).toList()
                )
                .projects(
                        projectRepository
                                .findByJobseekerIdOrderByDisplayOrderAscStartYearDesc(jobseeker.getId())
                                .stream().map(this::toProjectDTO).toList()
                )
                .certifications(
                        certificationRepository
                                .findByJobseekerIdOrderByDisplayOrderAscIssueYearDesc(jobseeker.getId())
                                .stream().map(this::toCertDTO).toList()
                )
                // Delegates to JobPreferenceService — single source of truth
                .jobPreference(jobPreferenceService.getByJobseekerId(jobseeker.getId()))
                .build();
    }

    // ─────────────────────────────────────────────
    // Basic info
    // ─────────────────────────────────────────────

    @Transactional
    public JobseekerProfileDTO updateBasicInfo(String email, JobseekerBasicInfoRequest request) {
        Jobseeker jobseeker = resolveJobseeker(email);

        if (request.getFullName() != null)          jobseeker.setFullName(request.getFullName());
        if (request.getPhone() != null)             jobseeker.setPhone(request.getPhone());
        if (request.getHeadline() != null)          jobseeker.setHeadline(request.getHeadline());
        if (request.getSummary() != null)           jobseeker.setSummary(request.getSummary());
        if (request.getLinkedinUrl() != null)       jobseeker.setLinkedinUrl(request.getLinkedinUrl());
        if (request.getGithubUrl() != null)         jobseeker.setGithubUrl(request.getGithubUrl());
        if (request.getPortfolioUrl() != null)      jobseeker.setPortfolioUrl(request.getPortfolioUrl());
        if (request.getYearsOfExperience() != null) jobseeker.setYearsOfExperience(request.getYearsOfExperience());
        if (request.getIsOnCareerBreak() != null)   jobseeker.setIsOnCareerBreak(request.getIsOnCareerBreak());

        recalculateCompletion(jobseeker);
        jobseekerRepository.save(jobseeker);

        return getFullProfile(email);
    }

    // ─────────────────────────────────────────────
    // File uploads
    // ─────────────────────────────────────────────

    @Transactional
    public String uploadResume(String email, MultipartFile file) {
        Jobseeker jobseeker = resolveJobseeker(email);

        if (jobseeker.getResumeUrl() != null) {
            r2StorageService.delete(jobseeker.getResumeUrl());
        }

        String url = r2StorageService.uploadResume(file, jobseeker.getUserId());
        jobseeker.setResumeUrl(url);
        jobseeker.setResumeFileName(file.getOriginalFilename());

        recalculateCompletion(jobseeker);
        jobseekerRepository.save(jobseeker);

        log.info("Resume uploaded for: {}", email);
        return url;
    }

    @Transactional
    public String uploadProfilePhoto(String email, MultipartFile file) {
        Jobseeker jobseeker = resolveJobseeker(email);

        if (jobseeker.getProfilePhotoUrl() != null) {
            r2StorageService.delete(jobseeker.getProfilePhotoUrl());
        }

        String url = r2StorageService.uploadProfilePhoto(file, jobseeker.getUserId());
        jobseeker.setProfilePhotoUrl(url);

        recalculateCompletion(jobseeker);
        jobseekerRepository.save(jobseeker);

        log.info("Profile photo uploaded for: {}", email);
        return url;
    }

    // ─────────────────────────────────────────────
    // Profile completion
    // ─────────────────────────────────────────────

    void recalculateCompletion(Jobseeker jobseeker) {
        int score = 0;

        if (isNotEmpty(jobseeker.getFullName()))        score += 15;
        if (isNotEmpty(jobseeker.getHeadline()))        score += 10;
        if (isNotEmpty(jobseeker.getSummary()))         score += 10;
        if (isNotEmpty(jobseeker.getPhone()))           score += 5;
        if (isNotEmpty(jobseeker.getProfilePhotoUrl())) score += 10;
        if (isNotEmpty(jobseeker.getResumeUrl()))       score += 20;
        if (isNotEmpty(jobseeker.getLinkedinUrl()))     score += 5;

        if (!workExperienceRepository
                .findByJobseekerIdOrderByDisplayOrderAscStartYearDesc(jobseeker.getId()).isEmpty()) score += 10;
        if (!educationRepository
                .findByJobseekerIdOrderByDisplayOrderAscEndYearDesc(jobseeker.getId()).isEmpty())   score += 10;
        if (!skillRepository
                .findByJobseekerIdOrderByDisplayOrderAsc(jobseeker.getId()).isEmpty())              score += 5;

        jobseeker.setProfileCompletion(Math.min(score, 100));
    }

    // ─────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────

    public Jobseeker resolveJobseeker(String email) {
        return jobseekerRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Jobseeker profile not found"));
    }
    private boolean isNotEmpty(String value) {
        return value != null && !value.isBlank();
    }

    private WorkExperienceDTO toWorkExpDTO(WorkExperience e) {
        return WorkExperienceDTO.builder()
                .id(e.getId()).company(e.getCompany()).title(e.getTitle())
                .location(e.getLocation()).employmentType(e.getEmploymentType())
                .startMonth(e.getStartMonth()).startYear(e.getStartYear())
                .endMonth(e.getEndMonth()).endYear(e.getEndYear())
                .isCurrent(e.getIsCurrent()).description(e.getDescription())
                .skillsUsed(e.getSkillsUsed()).displayOrder(e.getDisplayOrder())
                .build();
    }

    private EducationDTO toEducationDTO(Education e) {
        return EducationDTO.builder()
                .id(e.getId()).institution(e.getInstitution()).degree(e.getDegree())
                .fieldOfStudy(e.getFieldOfStudy()).startYear(e.getStartYear())
                .endYear(e.getEndYear()).grade(e.getGrade()).gradeType(e.getGradeType())
                .description(e.getDescription()).displayOrder(e.getDisplayOrder())
                .build();
    }

    private SkillDTO toSkillDTO(Skill s) {
        return SkillDTO.builder()
                .id(s.getId()).name(s.getName())
                .proficiencyLevel(s.getProficiencyLevel())
                .yearsOfExperience(s.getYearsOfExperience())
                .displayOrder(s.getDisplayOrder())
                .build();
    }

    private ProjectDTO toProjectDTO(Project p) {
        return ProjectDTO.builder()
                .id(p.getId()).title(p.getTitle()).description(p.getDescription())
                .techStack(p.getTechStack()).projectUrl(p.getProjectUrl()).repoUrl(p.getRepoUrl())
                .startMonth(p.getStartMonth()).startYear(p.getStartYear())
                .endMonth(p.getEndMonth()).endYear(p.getEndYear())
                .isOngoing(p.getIsOngoing()).displayOrder(p.getDisplayOrder())
                .build();
    }

    private CertificationDTO toCertDTO(Certification c) {
        return CertificationDTO.builder()
                .id(c.getId()).name(c.getName()).issuer(c.getIssuer())
                .issueMonth(c.getIssueMonth()).issueYear(c.getIssueYear())
                .expiryMonth(c.getExpiryMonth()).expiryYear(c.getExpiryYear())
                .doesNotExpire(c.getDoesNotExpire()).credentialId(c.getCredentialId())
                .credentialUrl(c.getCredentialUrl()).displayOrder(c.getDisplayOrder())
                .build();
    }
}