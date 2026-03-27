package com.iitbase.recruiter.dto.response;

import com.iitbase.recruiter.entity.Recruiter;
import com.iitbase.recruiter.enums.TeamMemberRole;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TeamMemberResponse {
    private Long id;
    private Long userId;
    private String name;
    private String designation;
    private TeamMemberRole role;
    private LocalDateTime joinedAt;

    public static TeamMemberResponse from(Recruiter r) {
        return TeamMemberResponse.builder()
                .id(r.getId())
                .userId(r.getUserId())
                .name(r.getName())
                .designation(r.getDesignation())
                .role(r.getRole())
                .joinedAt(r.getCreatedAt())
                .build();
    }
}