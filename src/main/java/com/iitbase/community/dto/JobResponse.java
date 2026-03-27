package com.iitbase.community.dto;

import com.iitbase.community.entity.CommunityJob;
import com.iitbase.community.enums.JobStatus;
import com.iitbase.community.enums.JobDomain;
import com.iitbase.community.enums.TechRole;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobResponse {
    private Long id;
    private String title;
    private String company;
    private String location;
    private String applyUrl;
    private String sourceUrl;
    private Integer minExperience;
    private Integer maxExperience;
    private JobDomain jobDomain;
    private TechRole techRole;
    private String roleTitle;
    private String jobDescription;
    private List<String> techStack;
    private List<String> skills;
    private String tierOneReason;
    private String createdAt;
    private JobStatus status;

    public static JobResponse from(CommunityJob job) {
        return JobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .company(job.getCompany())
                .location(job.getLocation())
                .applyUrl(job.getApplyUrl())
                .sourceUrl(job.getSourceUrl())
                .minExperience(job.getMinExperience())
                .maxExperience(job.getMaxExperience())
                .jobDomain(job.getJobDomain())
                .techRole(job.getTechRole())
                .roleTitle(job.getRoleTitle())
                .jobDescription(job.getJobDescription())
                .techStack(job.getTechStack())
                .skills(job.getSkills())
                .tierOneReason(job.getTierOneReason())
                .status(job.getStatus())
                .createdAt(job.getCreatedAt().toLocalDate().toString())
                .build();
    }
}