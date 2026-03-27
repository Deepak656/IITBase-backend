package com.iitbase.admin.company.dto;

import com.iitbase.recruiter.enums.CompanySize;
import lombok.Data;

@Data
public class AdminEditCompanyRequest {
    private String name;
    private String website;
    private String industry;
    private CompanySize size;
    private String description;
    private String logoUrl;
}