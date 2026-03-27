package com.iitbase.jobseeker.model;

import com.iitbase.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "educations", indexes = {
        @Index(name = "idx_education_jobseeker", columnList = "jobseeker_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Education extends BaseEntity {

    @Column(name = "jobseeker_id", nullable = false)
    private Long jobseekerId;

    @Column(nullable = false)
    private String institution;  // IIT Bombay, IIT Delhi etc.

    @Column(nullable = false)
    private String degree;  // B.Tech, M.Tech, MBA, PhD

    private String fieldOfStudy;  // Mechanical Engineering, CS, etc.

    @Column(name = "start_year")
    private Integer startYear;

    @Column(name = "end_year")
    private Integer endYear;

    @Column(name = "grade")
    private String grade;  // CPI, CGPA, percentage — keep as string for flexibility

    @Column(name = "grade_type", length = 20)
    private String gradeType;  // CGPA, PERCENTAGE, GPA

    @Column(columnDefinition = "TEXT")
    private String description;  // activities, societies, achievements

    @Column(name = "display_order")
    private Integer displayOrder;
}