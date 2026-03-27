package com.iitbase.admin.staff.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class StaffInviteRequest {

    @NotBlank
    @Email(message = "Must be a valid email address")
    private String email;
}