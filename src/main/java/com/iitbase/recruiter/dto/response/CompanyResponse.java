package com.iitbase.recruiter.dto.response;

import com.iitbase.recruiter.entity.Company;
import com.iitbase.recruiter.enums.CompanySize;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CompanyResponse {
    private Long id;
    private String name;
    private String website;
    private String industry;
    private CompanySize size;
    private String logoUrl;
    private String description;
    private Boolean isVerified;
    private LocalDateTime createdAt;

    public static CompanyResponse from(Company c) {
        return CompanyResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .website(c.getWebsite())
                .industry(c.getIndustry())
                .size(c.getSize())
                .logoUrl(c.getLogoUrl())
                .description(c.getDescription())
                .isVerified(c.getIsVerified())
                .createdAt(c.getCreatedAt())
                .build();
    }
}