package com.iitbase.job.dto;

import com.iitbase.job.Job;
import com.iitbase.job.JobStatus;
import com.iitbase.job.PrimaryRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private PrimaryRole primaryRole;
    private String jobDescription;
    private List<String> techStack;
    private List<String> skills;
    private String tierOneReason;
    private String createdAt;
    private JobStatus status;  // Add this for my jobs

    public static JobResponse from(Job job) {
        return JobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .company(job.getCompany())
                .location(job.getLocation())
                .minExperience(job.getMinExperience())
                .maxExperience(job.getMaxExperience())
                .primaryRole(job.getPrimaryRole())
                .jobDescription(job.getJobDescription())
                .techStack(job.getTechStack())
                .tierOneReason(job.getTierOneReason())
                .status(job.getStatus())
                .createdAt(job.getCreatedAt().toLocalDate().toString())
                .build();
    }
}