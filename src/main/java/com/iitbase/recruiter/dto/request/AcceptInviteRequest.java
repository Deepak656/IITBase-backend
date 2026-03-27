package com.iitbase.recruiter.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class AcceptInviteRequest {

    @NotBlank
    private String token;

    // Name and designation collected at acceptance time
    @NotBlank
    private String name;

    @NotBlank
    private String designation;
}