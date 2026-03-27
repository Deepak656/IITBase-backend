package com.iitbase.jobseeker.model;

import com.iitbase.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "certifications", indexes = {
        @Index(name = "idx_cert_jobseeker", columnList = "jobseeker_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Certification extends BaseEntity {

    @Column(name = "jobseeker_id", nullable = false)
    private Long jobseekerId;

    @Column(nullable = false)
    private String name;  // AWS Certified Solutions Architect

    @Column(nullable = false)
    private String issuer;  // Amazon Web Services

    @Column(name = "issue_month")
    private Integer issueMonth;

    @Column(name = "issue_year")
    private Integer issueYear;

    @Column(name = "expiry_month")
    private Integer expiryMonth;

    @Column(name = "expiry_year")
    private Integer expiryYear;

    @Column(name = "does_not_expire")
    @Builder.Default
    private Boolean doesNotExpire = false;

    @Column(name = "credential_id")
    private String credentialId;

    @Column(name = "credential_url")
    private String credentialUrl;

    @Column(name = "display_order")
    private Integer displayOrder;
}