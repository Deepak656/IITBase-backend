package com.iitbase.community.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyJobsStatsResponse {
    private long total;
    private long pending;
    private long approved;
    private long rejected;
    private long underReview;
    private long expired;
}
