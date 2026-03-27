package com.iitbase.application.dto.response;

import com.iitbase.application.entity.Application;
import com.iitbase.application.enums.ApplicationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ApplicationResponse {
    private Long id;
    private Long recruiterJobId;
    private String jobTitle;
    private String companyName;
    private ApplicationStatus status;
    private String coverNote;
    private String resumeUrl;
    private LocalDateTime appliedAt;

    // Recruiter notes deliberately excluded — jobseeker cannot see these

    public static ApplicationResponse from(Application a) {
        return ApplicationResponse.builder()
                .id(a.getId())
                .recruiterJobId(a.getRecruiterJobId())
                .jobTitle(a.getJobTitle())
                .companyName(a.getCompanyName())
                .status(a.getStatus())
                .coverNote(a.getCoverNote())
                .resumeUrl(a.getResumeUrl())
                .appliedAt(a.getCreatedAt())
                .build();
    }
}