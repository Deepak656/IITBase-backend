package com.iitbase.recruiter.dto.response;

import com.iitbase.recruiter.entity.Company;
import com.iitbase.recruiter.enums.CompanyStatus;
import lombok.Builder;
import lombok.Getter;

/**
 * Lightweight response used in onboarding company search.
 * Includes admin info so the UI can show "Contact [name] to join".
 */
@Getter
@Builder
public class CompanySearchResult {
    private Long id;
    private String name;
    private String website;
    private String industry;
    private String logoUrl;
    private CompanyStatus status;
    private boolean domainMatch;        // true if user's email domain matches
    private String adminName;           // shown in "contact admin" message
    private String adminDesignation;

    public static CompanySearchResult from(Company c,
                                           boolean domainMatch,
                                           String adminName,
                                           String adminDesignation) {
        return CompanySearchResult.builder()
                .id(c.getId())
                .name(c.getName())
                .website(c.getWebsite())
                .industry(c.getIndustry())
                .logoUrl(c.getLogoUrl())
                .status(c.getStatus())
                .domainMatch(domainMatch)
                .adminName(adminName)
                .adminDesignation(adminDesignation)
                .build();
    }
}