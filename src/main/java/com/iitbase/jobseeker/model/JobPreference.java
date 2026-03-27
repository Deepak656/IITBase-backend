package com.iitbase.jobseeker.model;

import com.iitbase.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "jobseeker_job_preferences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPreference extends BaseEntity {

    @Column(name = "jobseeker_id", nullable = false, unique = true)
    private Long jobseekerId;

    @Column(name = "current_location")
    private String currentLocation;

    @Column(name = "work_location_type", length = 50)
    private String workLocationType;  // REMOTE, HYBRID, ONSITE

    @Column(name = "preferred_cities", columnDefinition = "text[]")
    private String[] preferredCities;

    @Column(name = "previous_salary", precision = 12, scale = 2)
    private BigDecimal previousSalary;

    @Column(name = "previous_salary_currency", length = 10)
    @Builder.Default
    private String previousSalaryCurrency = "INR";

    @Column(name = "expected_salary", precision = 12, scale = 2)
    private BigDecimal expectedSalary;

    @Column(name = "expected_salary_currency", length = 10)
    @Builder.Default
    private String expectedSalaryCurrency = "INR";

    @Column(name = "notice_period", length = 50)
    private String noticePeriod;  // IMMEDIATE, 15_DAYS, 30_DAYS, 60_DAYS, 90_DAYS

    @Column(name = "primary_role", length = 100)
    private String primaryRole;  // BACKEND, FRONTEND, FULLSTACK, DATA, DEVOPS etc.

    @Column(name = "open_to_roles", columnDefinition = "text[]")
    private String[] openToRoles;  // secondary roles they'd consider
}