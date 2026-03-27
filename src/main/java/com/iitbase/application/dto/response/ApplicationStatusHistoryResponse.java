package com.iitbase.application.dto.response;

import com.iitbase.application.entity.ApplicationStatusHistory;
import com.iitbase.application.enums.ApplicationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ApplicationStatusHistoryResponse {
    private ApplicationStatus fromStatus;
    private ApplicationStatus toStatus;
    private String note;
    private LocalDateTime changedAt;

    // changedBy intentionally omitted from jobseeker view
    // exposed only in recruiter view if needed later

    public static ApplicationStatusHistoryResponse from(
            ApplicationStatusHistory h) {
        return ApplicationStatusHistoryResponse.builder()
                .fromStatus(h.getFromStatus())
                .toStatus(h.getToStatus())
                .note(h.getNote())
                .changedAt(h.getCreatedAt())
                .build();
    }
}