package com.iitbase.recruiter.entity;

import com.iitbase.common.BaseEntity;
import com.iitbase.recruiter.enums.CompanySize;
import com.iitbase.recruiter.enums.CompanyStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "companies")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Company extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    private String website;

    // Extracted from work email at creation time e.g. "google.com"
    // Null if created without a verified work email
    @Column(name = "email_domain")
    private String emailDomain;

    private String industry;

    @Enumerated(EnumType.STRING)
    private CompanySize size;

    private String logoUrl;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Legacy field — kept for backward compat, driven by status now
    @Column(nullable = false)
    private Boolean isVerified = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private CompanyStatus status = CompanyStatus.PENDING;
    @Column(name = "created_by_user_id")
    private Long createdByUserId;
    // userId of the IITBase admin who verified/rejected
    private Long reviewedBy;

}