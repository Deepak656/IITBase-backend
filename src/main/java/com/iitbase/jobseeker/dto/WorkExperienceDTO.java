package com.iitbase.jobseeker.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkExperienceDTO {

    private Long id;

    @NotBlank(message = "Company name is required")
    private String company;

    @NotBlank(message = "Job title is required")
    private String title;

    private String location;

    private String employmentType;  // FULL_TIME, PART_TIME, CONTRACT, INTERNSHIP, FREELANCE

    @Min(1) @Max(12)
    private Integer startMonth;

    @NotNull(message = "Start year is required")
    private Integer startYear;

    @Min(1) @Max(12)
    private Integer endMonth;

    private Integer endYear;

    private Boolean isCurrent;

    private String description;

    private String skillsUsed;

    private Integer displayOrder;
}