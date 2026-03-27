package com.iitbase.email.otp;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResendOtpRequest {

    @Email
    @NotBlank
    private String email;

    @NotNull
    private OtpPurpose purpose;
}

