package com.iitbase.recruiter.dto.request;

import com.iitbase.recruiter.enums.CompanySize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCompanyRequest {

    @NotBlank(message = "Company name is required")
    private String name;

    private String website;

    private String workEmail;
    @NotBlank(message = "Industry is required")
    private String industry;

    @NotNull(message = "Company size is required")
    private CompanySize size;
    private String description;
    private Long createdByUserId;
}
