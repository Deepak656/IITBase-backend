package com.iitbase.recruiter.dto.request;

import com.iitbase.recruiter.enums.TeamMemberRole;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class UpdateTeamMemberRoleRequest {

    @NotNull
    private TeamMemberRole role;
}