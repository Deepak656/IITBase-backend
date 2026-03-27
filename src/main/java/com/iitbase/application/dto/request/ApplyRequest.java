package com.iitbase.application.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ApplyRequest {

    @NotNull(message = "Job ID is required")
    private Long recruiterJobId;

    @Size(max = 1000, message = "Cover note must be under 1000 characters")
    private String coverNote;     // optional
}