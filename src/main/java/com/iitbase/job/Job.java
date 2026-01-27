package com.iitbase.job;

import com.iitbase.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String company;

    @Column(length = 255)
    private String location;

    @Column(columnDefinition = "TEXT")
    private String jobDescription;

    @Column(nullable = false)
    private String applyUrl;

    private String sourceUrl;

    @Column(nullable = false)
    private Integer minExperience;

    @Column(nullable = false)
    private Integer maxExperience;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrimaryRole primaryRole;

    @ElementCollection
    @CollectionTable(name = "job_tech_stack", joinColumns = @JoinColumn(name = "job_id"))
    @Column(name = "tech")
    @Builder.Default
    private List<String> techStack = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "job_skills", joinColumns = @JoinColumn(name = "job_id"))
    @Column(name = "skill")
    @Builder.Default
    private List<String> skills = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String tierOneReason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private JobStatus status = JobStatus.PENDING;

    @Column(nullable = false)
    private Long submittedBy;
}