package com.iitbase.job.dto;

import com.iitbase.job.PrimaryRole;
import lombok.Data;

import java.util.List;

@Data
public class JobFilterRequest {
    private PrimaryRole role;
    private Integer minExperience;
    private Integer maxExperience;
    private String location;
    private String postedAfter;
    private List<String> techStack;
    private int page = 0;
    private int size = 20;
}