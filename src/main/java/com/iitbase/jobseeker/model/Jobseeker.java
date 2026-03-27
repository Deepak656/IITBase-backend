package com.iitbase.jobseeker.model;

import com.iitbase.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "jobseekers", indexes = {
        @Index(name = "idx_jobseeker_user_id", columnList = "user_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Jobseeker extends BaseEntity {

    @Column(name = "user_id", unique = true, nullable = false)
    private Long userId;  // FK to users.id

    @Column(nullable = false)
    private String email;

    @Column(name = "full_name")
    private String fullName;

    private String phone;

    private String headline;  // e.g. "Backend Engineer | IIT Bombay | 2+ years"

    @Column(columnDefinition = "TEXT")
    private String summary;  // short about me

    @Column(name = "profile_photo_url")
    private String profilePhotoUrl;

    @Column(name = "resume_url")
    private String resumeUrl;

    @Column(name = "resume_file_name")
    private String resumeFileName;  // original filename shown in UI

    @Column(name = "linkedin_url")
    private String linkedinUrl;

    @Column(name = "github_url")
    private String githubUrl;

    @Column(name = "portfolio_url")
    private String portfolioUrl;

    @Column(name = "years_of_experience")
    private Double yearsOfExperience;

    @Column(name = "is_on_career_break")
    @Builder.Default
    private Boolean isOnCareerBreak = false;

    @Column(name = "profile_completion")
    @Builder.Default
    private Integer profileCompletion = 0;  // 0-100, computed on save
    @Column(nullable = false)
    @Builder.Default
    private Boolean isVerified = false;

    private LocalDateTime verifiedAt;
}