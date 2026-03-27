package com.iitbase.application.dto.request;

import com.iitbase.application.enums.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateApplicationStatusRequest {

    @NotNull(message = "Status is required")
    private ApplicationStatus status;

    @Size(max = 2000, message = "Note must be under 2000 characters")
    private String note;           // optional recruiter note on transition
}