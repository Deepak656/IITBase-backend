package com.iitbase.recruiter.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class JoinCompanyRequest {

    @NotNull(message = "Company ID is required")
    private Long companyId;

    // Optional message to the company admin
    private String message;

    // Work email at this company — shown to admin as proof
    private String workEmail;
}