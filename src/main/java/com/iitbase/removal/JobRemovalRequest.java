package com.iitbase.removal;

import com.iitbase.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "job_removal_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobRemovalRequest extends BaseEntity {

    @Column(nullable = false)
    private Long jobId;

    @Column(nullable = false)
    private String requesterEmail;

    @Column(nullable = false)
    private Long requestedBy;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private RemovalStatus status = RemovalStatus.PENDING;
}