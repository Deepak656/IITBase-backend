package com.iitbase.admin.detail;

import com.iitbase.admin.detail.dto.*;
import com.iitbase.exception.ResourceNotFoundException;
import com.iitbase.community.entity.CommunityJob;
import com.iitbase.community.repository.CommunityJobRepository;
import com.iitbase.jobseeker.model.Jobseeker;
import com.iitbase.jobseeker.repository.JobseekerRepository;
import com.iitbase.recruiter.entity.Company;
import com.iitbase.recruiter.entity.Recruiter;
import com.iitbase.recruiter.entity.RecruiterJob;
import com.iitbase.recruiter.repository.CompanyRepository;
import com.iitbase.recruiter.repository.RecruiterJobRepository;
import com.iitbase.recruiter.repository.RecruiterRepository;
import com.iitbase.user.User;
import com.iitbase.user.UserRepository;
import com.iitbase.user.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminDetailService {

    private final CommunityJobRepository  jobRepository;
    private final RecruiterJobRepository  recruiterJobRepository;
    private final RecruiterRepository   recruiterRepository;
    private final CompanyRepository     companyRepository;
    private final JobseekerRepository   jobseekerRepository;
    private final UserRepository        userRepository;

    // ── Community job detail ──────────────────────────────────────────────

    public CommunityJobDetailResponse getCommunityJobDetail(Long jobId) {
        CommunityJob job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found: " + jobId));

        CommunityJobDetailResponse.PosterInfo poster = buildPosterInfo(job.getSubmittedBy());

        return CommunityJobDetailResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .company(job.getCompany())
                .location(job.getLocation())
                .jobDescription(job.getJobDescription())
                .applyUrl(job.getApplyUrl())
                .sourceUrl(job.getSourceUrl())
                .minExperience(job.getMinExperience())
                .maxExperience(job.getMaxExperience())
                .jobDomain(job.getJobDomain().name())
                .techRole(job.getTechRole() != null ? job.getTechRole().name() : null)
                .roleTitle(job.getRoleTitle())
                .tierOneReason(job.getTierOneReason())
                .status(job.getStatus().name())
                .techStack(job.getTechStack())
                .skills(job.getSkills())
                .createdAt(job.getCreatedAt())
                .poster(poster)
                .build();
    }

    // ── Recruiter job detail ──────────────────────────────────────────────

    public RecruiterJobDetailResponse getRecruiterJobDetail(Long jobId) {
        RecruiterJob job = recruiterJobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("RecruiterJob not found: " + jobId));

        Recruiter recruiter = job.getRecruiter();
        Company   company   = job.getCompany();

        String recruiterEmail = userRepository.findById(recruiter.getUserId())
                .map(User::getEmail).orElse("—");

        return RecruiterJobDetailResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .roleTitle(job.getRoleTitle())
                .jobDomain(job.getJobDomain().name())
                .techRole(job.getTechRole() != null ? job.getTechRole().name() : null)
                .location(job.getLocation())
                .jobDescription(job.getJobDescription())
                .minExperience(job.getMinExperience())
                .maxExperience(job.getMaxExperience())
                .applyType(job.getApplyType().name())
                .applyUrl(job.getApplyUrl())
                .salaryMin(job.getSalaryMin())
                .salaryMax(job.getSalaryMax())
                .currency(job.getCurrency())
                .status(job.getStatus().name())
                .techStack(job.getTechStack())
                .skills(job.getSkills())
                .createdAt(job.getCreatedAt())
                .expiresAt(job.getExpiresAt())
                .recruiter(RecruiterJobDetailResponse.RecruiterInfo.builder()
                        .id(recruiter.getId())
                        .userId(recruiter.getUserId())
                        .email(recruiterEmail)
                        .name(recruiter.getName())
                        .designation(recruiter.getDesignation())
                        .workEmail(recruiter.getWorkEmail())
                        .phone(recruiter.getPhone())
                        .role(recruiter.getRole().name())
                        .build())
                .company(RecruiterJobDetailResponse.CompanyInfo.builder()
                        .id(company.getId())
                        .name(company.getName())
                        .website(company.getWebsite())
                        .industry(company.getIndustry())
                        .size(company.getSize() != null ? company.getSize().name() : null)
                        .isVerified(company.getIsVerified())
                        .status(company.getStatus().name())
                        .emailDomain(company.getEmailDomain())
                        .build())
                .build();
    }

    // ── Company detail ────────────────────────────────────────────────────

    public AdminCompanyDetailResponse getCompanyDetail(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found: " + companyId));

        // Who created the company
        AdminCompanyDetailResponse.CreatorInfo creatorInfo = null;
        if (company.getCreatedByUserId() != null) {
            creatorInfo = buildCreatorInfo(company.getCreatedByUserId(), companyId);
        }

        // All recruiters
        List<AdminCompanyDetailResponse.RecruiterSummary> recruiterList =
                recruiterRepository.findAllByCompanyId(companyId)
                        .stream()
                        .map(r -> {
                            String email = userRepository.findById(r.getUserId())
                                    .map(User::getEmail).orElse("—");
                            return AdminCompanyDetailResponse.RecruiterSummary.builder()
                                    .id(r.getId())
                                    .email(email)
                                    .name(r.getName())
                                    .designation(r.getDesignation())
                                    .workEmail(r.getWorkEmail())
                                    .phone(r.getPhone())
                                    .role(r.getRole().name())
                                    .build();
                        })
                        .toList();

        return AdminCompanyDetailResponse.builder()
                .id(company.getId())
                .name(company.getName())
                .website(company.getWebsite())
                .industry(company.getIndustry())
                .size(company.getSize() != null ? company.getSize().name() : null)
                .logoUrl(company.getLogoUrl())
                .description(company.getDescription())
                .isVerified(company.getIsVerified())
                .status(company.getStatus().name())
                .emailDomain(company.getEmailDomain())
                .createdAt(company.getCreatedAt())
                .createdBy(creatorInfo)
                .recruiters(recruiterList)
                .build();
    }

    // ── Recruiter detail ──────────────────────────────────────────────────

    public AdminRecruiterDetailResponse getRecruiterDetail(Long recruiterId) {
        Recruiter recruiter = recruiterRepository.findById(recruiterId)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter not found: " + recruiterId));

        String email = userRepository.findById(recruiter.getUserId())
                .map(User::getEmail).orElse("—");

        Company company = recruiter.getCompany();

        // Jobs posted by this recruiter
        List<AdminRecruiterDetailResponse.JobSummary> jobs =
                recruiterJobRepository.findByRecruiterId(recruiterId)
                        .stream()
                        .map(j -> AdminRecruiterDetailResponse.JobSummary.builder()
                                .id(j.getId())
                                .title(j.getTitle())
                                .roleTitle(j.getRoleTitle())
                                .status(j.getStatus().name())
                                .applyType(j.getApplyType().name())
                                .createdAt(j.getCreatedAt())
                                .build())
                        .toList();

        return AdminRecruiterDetailResponse.builder()
                .id(recruiter.getId())
                .userId(recruiter.getUserId())
                .email(email)
                .name(recruiter.getName())
                .designation(recruiter.getDesignation())
                .role(recruiter.getRole().name())
                .workEmail(recruiter.getWorkEmail())
                .phone(recruiter.getPhone())
                .isSuspended(false)   // derived from token invalidation; no persistent state yet
                .createdAt(recruiter.getCreatedAt())
                .company(AdminRecruiterDetailResponse.CompanyInfo.builder()
                        .id(company.getId())
                        .name(company.getName())
                        .website(company.getWebsite())
                        .industry(company.getIndustry())
                        .size(company.getSize() != null ? company.getSize().name() : null)
                        .isVerified(company.getIsVerified())
                        .status(company.getStatus().name())
                        .emailDomain(company.getEmailDomain())
                        .createdAt(company.getCreatedAt())
                        .build())
                .jobs(jobs)
                .build();
    }

    // ── Jobseeker detail ──────────────────────────────────────────────────

    public AdminJobseekerDetailResponse getJobseekerDetail(Long jobseekerId) {
        Jobseeker js = jobseekerRepository.findById(jobseekerId)
                .orElseThrow(() -> new ResourceNotFoundException("Jobseeker not found: " + jobseekerId));

        return AdminJobseekerDetailResponse.builder()
                .id(js.getId())
                .userId(js.getUserId())
                .email(js.getEmail())
                .fullName(js.getFullName())
                .phone(js.getPhone())
                .headline(js.getHeadline())
                .summary(js.getSummary())
                .profilePhotoUrl(js.getProfilePhotoUrl())
                .resumeUrl(js.getResumeUrl())
                .resumeFileName(js.getResumeFileName())
                .linkedinUrl(js.getLinkedinUrl())
                .githubUrl(js.getGithubUrl())
                .portfolioUrl(js.getPortfolioUrl())
                .yearsOfExperience(js.getYearsOfExperience())
                .isOnCareerBreak(js.getIsOnCareerBreak())
                .profileCompletion(js.getProfileCompletion())
                .isVerified(js.getIsVerified())
                .verifiedAt(js.getVerifiedAt())
                .createdAt(js.getCreatedAt())
                .build();
    }

    // ── Private helpers ───────────────────────────────────────────────────

    private CommunityJobDetailResponse.PosterInfo buildPosterInfo(Long userId) {
        if (userId == null) return null;

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return null;

        CommunityJobDetailResponse.PosterInfo.PosterInfoBuilder builder =
                CommunityJobDetailResponse.PosterInfo.builder()
                        .userId(user.getId())
                        .email(user.getEmail())
                        .role(user.getRole().name());

        // Enrich with jobseeker profile if poster is a job seeker
        if (user.getRole() == UserRole.JOB_SEEKER) {
            jobseekerRepository.findByUserId(user.getId()).ifPresent(js -> {
                builder.fullName(js.getFullName())
                        .phone(js.getPhone())
                        .headline(js.getHeadline())
                        .linkedinUrl(js.getLinkedinUrl())
                        .profileCompletion(js.getProfileCompletion())
                        .isVerified(js.getIsVerified());
            });
        }

        return builder.build();
    }

    private AdminCompanyDetailResponse.CreatorInfo buildCreatorInfo(Long userId, Long companyId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return null;

        // Find this user's recruiter profile at this company (to get workEmail/phone)
        Recruiter recruiter = recruiterRepository.findAllByCompanyId(companyId)
                .stream()
                .filter(r -> r.getUserId().equals(userId))
                .findFirst()
                .orElse(null);

        return AdminCompanyDetailResponse.CreatorInfo.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .name(recruiter != null ? recruiter.getName() : null)
                .designation(recruiter != null ? recruiter.getDesignation() : null)
                .workEmail(recruiter != null ? recruiter.getWorkEmail() : null)
                .phone(recruiter != null ? recruiter.getPhone() : null)
                .build();
    }
}