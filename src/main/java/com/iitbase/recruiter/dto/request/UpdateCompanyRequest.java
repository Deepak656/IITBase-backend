package com.iitbase.recruiter.dto.request;

import com.iitbase.recruiter.enums.CompanySize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCompanyRequest {
    private String website;
    private String industry;
    private CompanySize size;
    private String description;
    private String logoUrl;
}
