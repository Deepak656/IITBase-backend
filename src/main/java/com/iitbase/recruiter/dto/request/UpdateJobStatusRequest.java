package com.iitbase.recruiter.dto.request;

import com.iitbase.recruiter.enums.RecruiterJobStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateJobStatusRequest {

    @NotNull
    private RecruiterJobStatus status;
}
