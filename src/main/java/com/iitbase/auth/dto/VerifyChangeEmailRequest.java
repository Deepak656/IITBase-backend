package com.iitbase.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class VerifyChangeEmailRequest {
    private String newEmail;
    private String otp;
}
