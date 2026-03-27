package com.iitbase.application.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SendInviteRequest {

    @NotNull(message = "Jobseeker ID is required")
    private Long jobseekerId;

    @NotNull(message = "Job ID is required")
    private Long recruiterJobId;

    @Size(max = 500, message = "Message must be under 500 characters")
    private String message;         // optional personal note
}