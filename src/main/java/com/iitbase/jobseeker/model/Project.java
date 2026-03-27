package com.iitbase.jobseeker.model;

import com.iitbase.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "projects", indexes = {
        @Index(name = "idx_project_jobseeker", columnList = "jobseeker_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project extends BaseEntity {

    @Column(name = "jobseeker_id", nullable = false)
    private Long jobseekerId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "tech_stack")
    private String techStack;  // "Java, Spring Boot, PostgreSQL, Redis"

    @Column(name = "project_url")
    private String projectUrl;  // live URL

    @Column(name = "repo_url")
    private String repoUrl;  // GitHub link

    @Column(name = "start_month")
    private Integer startMonth;

    @Column(name = "start_year")
    private Integer startYear;

    @Column(name = "end_month")
    private Integer endMonth;

    @Column(name = "end_year")
    private Integer endYear;

    @Column(name = "is_ongoing")
    @Builder.Default
    private Boolean isOngoing = false;

    @Column(name = "display_order")
    private Integer displayOrder;
}