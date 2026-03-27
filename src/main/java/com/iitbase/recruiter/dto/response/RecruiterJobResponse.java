package com.iitbase.recruiter.dto.response;

import com.iitbase.community.enums.JobDomain;
import com.iitbase.community.enums.TechRole;
import com.iitbase.recruiter.entity.RecruiterJob;
import com.iitbase.recruiter.enums.JobApplyType;
import com.iitbase.recruiter.enums.RecruiterJobStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class RecruiterJobResponse {
    private Long id;
    private Long recruiterId;
    private Long companyId;
    private String companyName;
    private String title;
    private String roleTitle;
    private JobDomain jobDomain;
    private TechRole techRole;
    private String location;
    private String jobDescription;
    private Integer minExperience;
    private Integer maxExperience;
    private JobApplyType applyType;
    private String applyUrl;
    private Long salaryMin;
    private Long salaryMax;
    private String currency;
    private List<String> techStack;
    private List<String> skills;
    private RecruiterJobStatus status;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;

    public static RecruiterJobResponse from(RecruiterJob j) {
        return RecruiterJobResponse.builder()
                .id(j.getId())
                .recruiterId(j.getRecruiter().getId())
                .companyId(j.getCompany().getId())
                .companyName(j.getCompany().getName())
                .title(j.getTitle())
                .roleTitle(j.getRoleTitle())
                .jobDomain(j.getJobDomain())
                .techRole(j.getTechRole())
                .location(j.getLocation())
                .jobDescription(j.getJobDescription())
                .minExperience(j.getMinExperience())
                .maxExperience(j.getMaxExperience())
                .applyType(j.getApplyType())
                .applyUrl(j.getApplyUrl())
                .salaryMin(j.getSalaryMin())
                .salaryMax(j.getSalaryMax())
                .currency(j.getCurrency())
                .techStack(j.getTechStack())
                .skills(j.getSkills())
                .status(j.getStatus())
                .expiresAt(j.getExpiresAt())
                .createdAt(j.getCreatedAt())
                .build();
    }
}