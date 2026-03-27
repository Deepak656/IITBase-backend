package com.iitbase.application.dto.response;

import com.iitbase.application.entity.JobInvite;
import com.iitbase.application.enums.InviteStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class JobInviteResponse {
    private Long id;
    private Long recruiterId;
    private Long jobseekerId;
    private Long recruiterJobId;
    private String jobTitle;
    private String companyName;
    private String message;
    private InviteStatus status;
    private Long applicationId;
    private LocalDateTime createdAt;

    public static JobInviteResponse from(JobInvite i) {
        return JobInviteResponse.builder()
                .id(i.getId())
                .recruiterId(i.getRecruiterId())
                .jobseekerId(i.getJobseekerId())
                .recruiterJobId(i.getRecruiterJobId())
                .jobTitle(i.getJobTitle())
                .companyName(i.getCompanyName())
                .message(i.getMessage())
                .status(i.getStatus())
                .applicationId(i.getApplicationId())
                .createdAt(i.getCreatedAt())
                .build();
    }
}