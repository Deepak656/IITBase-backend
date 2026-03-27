package com.iitbase.recruiter.dto.response;

import com.iitbase.recruiter.entity.Recruiter;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RecruiterProfileResponse {
    private Long id;
    private Long userId;
    private CompanyResponse company;
    private String name;
    private String designation;
    private Boolean isAdmin;
    private LocalDateTime createdAt;

    public static RecruiterProfileResponse from(Recruiter r) {
        return RecruiterProfileResponse.builder()
                .id(r.getId())
                .userId(r.getUserId())
                .company(CompanyResponse.from(r.getCompany()))
                .name(r.getName())
                .designation(r.getDesignation())
                .isAdmin(r.getIsAdmin())
                .createdAt(r.getCreatedAt())
                .build();
    }
}