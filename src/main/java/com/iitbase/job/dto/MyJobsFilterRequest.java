package com.iitbase.job.dto;

import com.iitbase.job.JobStatus;
import lombok.Data;

import java.util.List;

@Data
public class MyJobsFilterRequest {
    private List<JobStatus> statuses;  // null or empty = all statuses
    private int page = 0;
    private int size = 20;
}
