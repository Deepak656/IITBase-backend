package com.iitbase.email.otp;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_otps", indexes = {
        @Index(name = "idx_email_purpose", columnList = "email, purpose"),
        @Index(name = "idx_created_at", columnList = "createdAt"),
        @Index(name = "idx_ip_address", columnList = "ipAddress")
})
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailOtp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String otp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OtpPurpose purpose;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private boolean used;

    @Column(nullable = false)
    @Builder.Default
    private int attempts = 0;

    @Column(nullable = false)
    @Builder.Default
    private int resendCount = 0;

    private LocalDateTime lockedUntil;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime lastSentAt;

    private LocalDateTime verifiedAt;

    @Column(nullable = false)
    private String ipAddress;  // Track IP for rate limiting

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}