package com.iitbase.admin.company.dto;

import com.iitbase.recruiter.entity.Company;
import com.iitbase.recruiter.enums.CompanySize;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AdminCompanyResponse {
    private Long id;
    private String name;
    private String website;
    private String industry;
    private CompanySize size;
    private String logoUrl;
    private String description;
    private Boolean isVerified;
    private LocalDateTime createdAt;
    private Long recruiterCount;    // how many recruiters belong to this company

    public static AdminCompanyResponse from(Company c, long recruiterCount) {
        return AdminCompanyResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .website(c.getWebsite())
                .industry(c.getIndustry())
                .size(c.getSize())
                .logoUrl(c.getLogoUrl())
                .description(c.getDescription())
                .isVerified(c.getIsVerified())
                .createdAt(c.getCreatedAt())
                .recruiterCount(recruiterCount)
                .build();
    }
}