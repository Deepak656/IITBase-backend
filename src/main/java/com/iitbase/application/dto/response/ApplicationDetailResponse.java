package com.iitbase.application.dto.response;

import com.iitbase.application.entity.Application;
import com.iitbase.application.enums.ApplicationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ApplicationDetailResponse {
    private Long id;
    private Long recruiterJobId;
    private String jobTitle;
    private Long jobseekerId;
    private ApplicationStatus status;
    private String coverNote;
    private String resumeUrl;
    private String recruiterNotes;    // visible to recruiter only
    private LocalDateTime appliedAt;
    private LocalDateTime updatedAt;

    public static ApplicationDetailResponse from(Application a) {
        return ApplicationDetailResponse.builder()
                .id(a.getId())
                .recruiterJobId(a.getRecruiterJobId())
                .jobTitle(a.getJobTitle())
                .jobseekerId(a.getJobseekerId())
                .status(a.getStatus())
                .coverNote(a.getCoverNote())
                .resumeUrl(a.getResumeUrl())
                .recruiterNotes(a.getRecruiterNotes())
                .appliedAt(a.getCreatedAt())
                .updatedAt(a.getUpdatedAt())
                .build();
    }
}