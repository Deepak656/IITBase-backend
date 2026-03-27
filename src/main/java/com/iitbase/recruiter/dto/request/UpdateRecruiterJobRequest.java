package com.iitbase.recruiter.dto.request;

import com.iitbase.recruiter.enums.RecruiterJobStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UpdateRecruiterJobRequest {
    private String title;
    private String roleTitle;
    private String location;
    private String jobDescription;
    private Integer minExperience;
    private Integer maxExperience;
    private String applyUrl;
    private Long salaryMin;
    private Long salaryMax;
    private String currency;
    private List<String> techStack;
    private List<String> skills;
    private RecruiterJobStatus status;
    private LocalDateTime expiresAt;
}