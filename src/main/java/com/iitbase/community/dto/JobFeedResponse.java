package com.iitbase.community.dto;

import com.iitbase.community.entity.CommunityJob;
import com.iitbase.community.enums.JobDomain;
import com.iitbase.community.enums.JobSource;
import com.iitbase.community.enums.TechRole;
import com.iitbase.recruiter.entity.RecruiterJob;
import com.iitbase.recruiter.enums.JobApplyType;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class JobFeedResponse {

    private Long id;
    private JobSource source;          // COMMUNITY | RECRUITER_EXTERNAL | RECRUITER_DIRECT

    // Job details — common across all types
    private String title;
    private String roleTitle;
    private JobDomain jobDomain;
    private TechRole techRole;         // only for TECHNOLOGY domain
    private String company;            // free text for COMMUNITY, company name for RECRUITER
    private String location;
    private String jobDescription;
    private Integer minExperience;
    private Integer maxExperience;
    private List<String> techStack;
    private List<String> skills;
    private String createdAt;

    // Apply behaviour — frontend uses these two fields to render the CTA button
    private Boolean easyApply;         // true = apply on IITBase
    private String applyUrl;           // null when easyApply = true

    // Recruiter job extras — null for COMMUNITY jobs
    private Long companyId;            // for linking to company profile
    private Long recruiterId;
    private Long salaryMin;
    private Long salaryMax;
    private String currency;

    // Trust signal — frontend can show "Verified Company" badge
    private Boolean verifiedCompany;

    // ── Static factory: from community Job ──────────────────────────────────
    public static JobFeedResponse fromCommunityJob(CommunityJob job) {
        return JobFeedResponse.builder()
                .id(job.getId())
                .source(JobSource.COMMUNITY)
                .title(job.getTitle())
                .roleTitle(job.getRoleTitle())
                .jobDomain(job.getJobDomain())
                .techRole(job.getTechRole())
                .company(job.getCompany())
                .location(job.getLocation())
                .jobDescription(job.getJobDescription())
                .minExperience(job.getMinExperience())
                .maxExperience(job.getMaxExperience())
                .techStack(job.getTechStack())
                .skills(job.getSkills())
                .createdAt(job.getCreatedAt().toLocalDate().toString())
                .easyApply(false)
                .applyUrl(job.getApplyUrl())
                .companyId(null)
                .recruiterId(null)
                .salaryMin(null)
                .salaryMax(null)
                .currency(null)
                .verifiedCompany(false)
                .build();
    }

    // ── Static factory: from RecruiterJob ────────────────────────────────────
    public static JobFeedResponse fromRecruiterJob(RecruiterJob job) {
        boolean isInternal = job.getApplyType() == JobApplyType.INTERNAL;

        return JobFeedResponse.builder()
                .id(job.getId())
                .source(isInternal
                        ? JobSource.RECRUITER_DIRECT
                        : JobSource.RECRUITER_EXTERNAL)
                .title(job.getTitle())
                .roleTitle(job.getRoleTitle())
                .jobDomain(job.getJobDomain())
                .techRole(job.getTechRole())
                .company(job.getCompany().getName())
                .location(job.getLocation())
                .jobDescription(job.getJobDescription())
                .minExperience(job.getMinExperience())
                .maxExperience(job.getMaxExperience())
                .techStack(job.getTechStack())
                .skills(job.getSkills())
                .createdAt(job.getCreatedAt().toLocalDate().toString())
                .easyApply(isInternal)
                .applyUrl(isInternal ? null : job.getApplyUrl())
                .companyId(job.getCompany().getId())
                .recruiterId(job.getRecruiter().getId())
                .salaryMin(job.getSalaryMin())
                .salaryMax(job.getSalaryMax())
                .currency(job.getCurrency())
                .verifiedCompany(job.getCompany().getIsVerified())
                .build();
    }
}