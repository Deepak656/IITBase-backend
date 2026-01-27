package com.iitbase.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatsResponse {
    private String email;
    private String role;
    private Long activeSessions;
    private Long totalJobsSubmitted;
    private Long approvedJobs;
    private Long pendingJobs;
    private Long rejectedJobs;
}