package com.iitbase.jobseeker.model;

import com.iitbase.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.YearMonth;

@Entity
@Table(name = "work_experiences", indexes = {
        @Index(name = "idx_work_exp_jobseeker", columnList = "jobseeker_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkExperience extends BaseEntity {

    @Column(name = "jobseeker_id", nullable = false)
    private Long jobseekerId;

    @Column(nullable = false)
    private String company;

    @Column(nullable = false)
    private String title;  // job title / designation

    private String location;

    @Column(name = "employment_type", length = 50)
    private String employmentType;  // FULL_TIME, PART_TIME, CONTRACT, INTERNSHIP, FREELANCE

    @Column(name = "start_month")
    private Integer startMonth;

    @Column(name = "start_year", nullable = false)
    private Integer startYear;

    @Column(name = "end_month")
    private Integer endMonth;

    @Column(name = "end_year")
    private Integer endYear;

    @Column(name = "is_current")
    @Builder.Default
    private Boolean isCurrent = false;  // if true, end date is null

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "skills_used")
    private String skillsUsed;  // comma-separated, e.g. "Java, Spring Boot, PostgreSQL"

    @Column(name = "display_order")
    private Integer displayOrder;  // for manual reordering
}