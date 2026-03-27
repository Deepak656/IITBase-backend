package com.iitbase.recruiter.entity;

import com.iitbase.common.BaseEntity;
import com.iitbase.community.enums.JobDomain;
import com.iitbase.community.enums.TechRole;
import com.iitbase.recruiter.enums.JobApplyType;
import com.iitbase.recruiter.enums.RecruiterJobStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recruiter_jobs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RecruiterJob extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruiter_id", nullable = false)
    private Recruiter recruiter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String roleTitle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobDomain jobDomain;

    // Only set when jobDomain = TECHNOLOGY
    @Enumerated(EnumType.STRING)
    private TechRole techRole;

    @Column(length = 255)
    private String location;

    @Column(columnDefinition = "TEXT")
    private String jobDescription;

    @Column(nullable = false)
    private Integer minExperience;

    @Column(nullable = false)
    private Integer maxExperience;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobApplyType applyType;         // EXTERNAL | INTERNAL

    private String applyUrl;                // required if EXTERNAL, null if INTERNAL

    private Long salaryMin;
    private Long salaryMax;

    @Column(length = 10)
    private String currency = "INR";

    @ElementCollection
    @CollectionTable(name = "recruiter_job_tech_stack",
            joinColumns = @JoinColumn(name = "recruiter_job_id"))
    @Column(name = "tech")
    @Builder.Default
    private List<String> techStack = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "recruiter_job_skills",
            joinColumns = @JoinColumn(name = "recruiter_job_id"))
    @Column(name = "skill")
    @Builder.Default
    private List<String> skills = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private RecruiterJobStatus status = RecruiterJobStatus.ACTIVE;

    private LocalDateTime expiresAt;
}