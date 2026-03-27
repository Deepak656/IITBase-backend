package com.iitbase.admin.staff.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class AcceptStaffInviteRequest {

    @NotBlank
    private String token;

    // If the recipient doesn't have an account yet, these create one.
    // If they already have an account, only the token is used.
    private String password;    // optional — only for new accounts
}