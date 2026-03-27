package com.iitbase.community.dto;

import com.iitbase.community.enums.JobDomain;
import com.iitbase.community.enums.TechRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class JobCreateRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String company;

    @NotBlank
    private String location;

    @NotBlank
    private String applyUrl;

    private String sourceUrl;
    private String jobDescription;

    @NotNull
    private Integer minExperience;

    @NotNull
    private Integer maxExperience;

    @NotNull
    private JobDomain jobDomain;

    // Required only when jobDomain = TECHNOLOGY
    // Validated in service layer, not annotation
    private TechRole techRole;

    @NotBlank
    private String roleTitle;

    private List<String> techStack = new ArrayList<>();
    private List<String> skills = new ArrayList<>();

    @NotBlank
    private String tierOneReason;
}