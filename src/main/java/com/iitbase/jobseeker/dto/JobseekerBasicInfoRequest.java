package com.iitbase.jobseeker.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobseekerBasicInfoRequest {

    @Size(max = 100, message = "Name too long")
    private String fullName;

    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Invalid phone number")
    private String phone;

    @Size(max = 200, message = "Headline too long")
    private String headline;

    @Size(max = 2000, message = "Summary too long")
    private String summary;

    @Size(max = 500, message = "URL too long")
    private String linkedinUrl;

    @Size(max = 500, message = "URL too long")
    private String githubUrl;

    @Size(max = 500, message = "URL too long")
    private String portfolioUrl;

    private Double yearsOfExperience;

    private Boolean isOnCareerBreak;
}