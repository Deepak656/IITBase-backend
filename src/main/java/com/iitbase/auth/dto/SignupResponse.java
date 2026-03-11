package com.iitbase.auth.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SignupResponse {
    private String token;
    private String role;
    private long   userId;
}
