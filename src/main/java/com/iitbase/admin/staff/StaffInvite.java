package com.iitbase.admin.staff;

import com.iitbase.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "staff_invites")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StaffInvite extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String token;               // UUID sent in email link

    @Column(nullable = false, unique = true)
    private String email;               // who was invited

    // userId of the ADMIN who sent this invite
    @Column(nullable = false)
    private Long invitedByUserId;

    @Column(nullable = false)
    private String invitedByEmail;      // for display

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StaffInviteStatus status = StaffInviteStatus.PENDING;

    @Column(nullable = false)
    private LocalDateTime expiresAt;    // 7 days

    private LocalDateTime acceptedAt;

    // userId of the person who accepted
    private Long acceptedByUserId;
}