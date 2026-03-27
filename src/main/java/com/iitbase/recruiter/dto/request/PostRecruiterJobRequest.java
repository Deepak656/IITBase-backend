package com.iitbase.recruiter.dto.request;

import com.iitbase.community.enums.JobDomain;
import com.iitbase.community.enums.TechRole;
import com.iitbase.recruiter.enums.JobApplyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class PostRecruiterJobRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String roleTitle;

    @NotNull
    private JobDomain jobDomain;

    // Validated in service — required only when jobDomain = TECHNOLOGY
    private TechRole techRole;

    @NotBlank
    private String location;

    private String jobDescription;

    @NotNull
    private Integer minExperience;

    @NotNull
    private Integer maxExperience;

    @NotNull
    private JobApplyType applyType;

    // Required when applyType = EXTERNAL, validated in service
    private String applyUrl;

    private Long salaryMin;
    private Long salaryMax;
    private String currency = "INR";

    private List<String> techStack = new ArrayList<>();
    private List<String> skills = new ArrayList<>();

    private LocalDateTime expiresAt;
}
