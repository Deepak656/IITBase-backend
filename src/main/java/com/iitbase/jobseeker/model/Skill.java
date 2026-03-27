package com.iitbase.jobseeker.model;

import com.iitbase.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "skills", indexes = {
        @Index(name = "idx_skill_jobseeker", columnList = "jobseeker_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Skill extends BaseEntity {

    @Column(name = "jobseeker_id", nullable = false)
    private Long jobseekerId;

    @Column(nullable = false)
    private String name;  // Java, Spring Boot, PostgreSQL

    @Column(name = "proficiency_level", length = 20)
    private String proficiencyLevel;  // BEGINNER, INTERMEDIATE, ADVANCED, EXPERT

    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;

    @Column(name = "display_order")
    private Integer displayOrder;
}