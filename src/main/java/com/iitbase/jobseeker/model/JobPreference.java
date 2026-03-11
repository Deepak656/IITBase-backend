package com.iitbase.jobseeker.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "jobseeker_job_preferences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPreference {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "jobseeker_id", nullable = false, unique = true)
    private Long jobseekerId;
    
    @Column(name = "current_location")
    private String currentLocation;
    
    @Column(name = "work_location_type", length = 50)
    private String workLocationType;
    
    @Column(name = "preferred_cities", columnDefinition = "text[]")
    private String[] preferredCities;
    
    @Column(name = "previous_salary", precision = 12, scale = 2)
    private BigDecimal previousSalary;
    
    @Column(name = "previous_salary_currency", length = 10)
    private String previousSalaryCurrency = "INR";
    
    @Column(name = "notice_period", length = 50)
    private String noticePeriod;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
