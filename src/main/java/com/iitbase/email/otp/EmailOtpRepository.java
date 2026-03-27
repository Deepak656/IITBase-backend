package com.iitbase.email.otp;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EmailOtpRepository extends JpaRepository<EmailOtp, Long> {

    Optional<EmailOtp> findTopByEmailAndPurposeAndUsedFalseOrderByExpiresAtDesc(
            String email, OtpPurpose purpose
    );

    Optional<EmailOtp> findTopByEmailAndPurposeOrderByCreatedAtDesc(
            String email, OtpPurpose purpose
    );
    long countByEmailAndCreatedAtAfter(String email, LocalDateTime since);

    void deleteByExpiresAtBeforeAndUsedTrue(LocalDateTime before);
}

