package com.iitbase.community.dto;

import com.iitbase.community.enums.JobDomain;
import com.iitbase.community.enums.JobSource;
import com.iitbase.community.enums.TechRole;
import lombok.Data;

import java.util.List;

@Data
public class JobFeedFilterRequest {
    private JobDomain domain;
    private TechRole techRole;
    private JobSource source;          // optional — filter by COMMUNITY / RECRUITER_*
    private Integer minExperience;
    private Integer maxExperience;
    private String location;
    private String postedAfter;
    private List<String> techStack;
    private int page = 0;
    private int size = 20;
}