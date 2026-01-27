package com.iitbase.job.dto;

import com.iitbase.job.PrimaryRole;
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
    private PrimaryRole primaryRole;

    private List<String> techStack = new ArrayList<>();
    private List<String> skills = new ArrayList<>();

    @NotBlank
    private String tierOneReason;
}