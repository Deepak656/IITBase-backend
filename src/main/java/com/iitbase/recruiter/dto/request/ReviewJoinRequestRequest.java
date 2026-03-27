package com.iitbase.recruiter.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class ReviewJoinRequestRequest {
    // Optional rejection reason — only used when rejecting
    private String rejectionReason;
}