package com.iitbase.community.dto;

import com.iitbase.community.enums.JobDomain;
import com.iitbase.community.enums.TechRole;
import lombok.Data;
import java.util.List;

@Data
public class JobFilterRequest {
    private JobDomain domain;         // replaces role
    private TechRole techRole;        // only relevant when domain = TECHNOLOGY
    private Integer minExperience;
    private Integer maxExperience;
    private String location;
    private String postedAfter;
    private List<String> techStack;
    private int page = 0;
    private int size = 20;
}