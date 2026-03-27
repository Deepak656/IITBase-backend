package com.iitbase.jobseeker.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CertificationDTO {

    private Long id;

    @NotBlank(message = "Certification name is required")
    private String name;

    @NotBlank(message = "Issuer is required")
    private String issuer;

    private Integer issueMonth;

    private Integer issueYear;

    private Integer expiryMonth;

    private Integer expiryYear;

    private Boolean doesNotExpire;

    private String credentialId;

    private String credentialUrl;

    private Integer displayOrder;
}