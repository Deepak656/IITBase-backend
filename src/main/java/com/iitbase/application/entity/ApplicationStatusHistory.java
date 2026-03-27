package com.iitbase.application.entity;

import com.iitbase.application.enums.ApplicationStatus;
import com.iitbase.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "application_status_history")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ApplicationStatusHistory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus toStatus;

    // Who made the change — userId (recruiter or jobseeker)
    @Column(nullable = false)
    private Long changedBy;

    // Optional recruiter note at time of transition
    @Column(columnDefinition = "TEXT")
    private String note;
}