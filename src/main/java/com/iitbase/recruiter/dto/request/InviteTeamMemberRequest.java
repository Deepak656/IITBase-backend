package com.iitbase.recruiter.dto.request;

import com.iitbase.recruiter.enums.TeamMemberRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class InviteTeamMemberRequest {

    @NotBlank
    @Email(message = "Must be a valid email")
    private String email;

    private TeamMemberRole role = TeamMemberRole.MEMBER;
}