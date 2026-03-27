package com.iitbase.jobseeker.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillDTO {

    private Long id;

    @NotBlank(message = "Skill name is required")
    private String name;

    private String proficiencyLevel;  // BEGINNER, INTERMEDIATE, ADVANCED, EXPERT

    private Integer yearsOfExperience;

    private Integer displayOrder;
}