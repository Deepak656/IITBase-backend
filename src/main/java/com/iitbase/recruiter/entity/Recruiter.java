package com.iitbase.recruiter.entity;

import com.iitbase.common.BaseEntity;
import com.iitbase.recruiter.enums.TeamMemberRole;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "recruiters")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Recruiter extends BaseEntity {

    @Column(nullable = false, unique = true)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "name")
    private String name;

    @Column(name = "work_email")
    private String workEmail;

    @Column(name = "phone", length = 30)
    private String phone;

    private String designation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TeamMemberRole role = TeamMemberRole.MEMBER;

    // Convenience helper — keeps existing callers working
    public Boolean getIsAdmin() {
        return this.role == TeamMemberRole.ADMIN;
    }
}