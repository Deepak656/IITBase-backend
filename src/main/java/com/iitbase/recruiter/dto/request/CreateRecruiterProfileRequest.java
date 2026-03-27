package com.iitbase.recruiter.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class CreateRecruiterProfileRequest {

    @NotNull(message = "Company ID is required")
    private Long companyId;          // Long, not UUID

    @NotBlank(message = "Designation is required")
    private String designation;

    @NotBlank(message = "Name is required")
    private String name;

    private String workEmail;

    private String phone;
}