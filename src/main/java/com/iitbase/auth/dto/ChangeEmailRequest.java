package com.iitbase.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeEmailRequest {
    private String newEmail;
}
