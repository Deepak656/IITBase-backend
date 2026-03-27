package com.iitbase.jobseeker.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDTO {

    private Long id;

    @NotBlank(message = "Project title is required")
    private String title;

    private String description;

    private String techStack;

    private String projectUrl;

    private String repoUrl;

    private Integer startMonth;

    private Integer startYear;

    private Integer endMonth;

    private Integer endYear;

    private Boolean isOngoing;

    private Integer displayOrder;
}