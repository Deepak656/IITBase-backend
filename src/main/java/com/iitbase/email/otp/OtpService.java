package com.iitbase.email.otp;

import com.iitbase.email.EmailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpService {

    private final EmailOtpRepository otpRepository;
    private final EmailService emailService;
    private final OtpRateLimitService rateLimitService;  // NEW: Rate limiting

    private static final int MAX_DAILY_OTP_PER_EMAIL = 5;
    private static final int MAX_ATTEMPTS = 5;
    private static final int MAX_RESEND_COUNT = 3;
    private static final int OTP_VALIDITY_MINUTES = 10;
    private static final int RESEND_COOLDOWN_SECONDS = 60;
    private static final int LOCKOUT_MINUTES = 15;

    @Transactional
    public void generateAndSendOtp(String email, OtpPurpose purpose, String ipAddress) {

        // 1️⃣ IP-based rate limiting (prevent OTP bombing)
        rateLimitService.checkIpRateLimit(ipAddress);

        // 2️⃣ Check daily limit per email
        long dailyCount = otpRepository.countByEmailAndCreatedAtAfter(
                email,
                LocalDateTime.now().minusDays(1)
        );

        if (dailyCount >= MAX_DAILY_OTP_PER_EMAIL) {
            log.warn("Daily OTP limit exceeded for email: {}", email);
            throw new IllegalArgumentException("Daily OTP limit reached. Please try tomorrow.");
        }

        // 3️⃣ Check last OTP cooldown
        EmailOtp lastOtp = otpRepository
                .findTopByEmailAndPurposeOrderByCreatedAtDesc(email, purpose)
                .orElse(null);

        if (lastOtp != null &&
                lastOtp.getCreatedAt().isAfter(LocalDateTime.now().minusSeconds(RESEND_COOLDOWN_SECONDS))) {

            long secondsRemaining = RESEND_COOLDOWN_SECONDS -
                    java.time.Duration.between(lastOtp.getCreatedAt(), LocalDateTime.now()).getSeconds();

            throw new IllegalArgumentException(
                    String.format("Please wait %d seconds before requesting another OTP", secondsRemaining)
            );
        }

        // 4️⃣ Generate secure OTP
        String otp = generateSecureOtp();

        // 5️⃣ Save OTP
        EmailOtp emailOtp = EmailOtp.builder()
                .email(email)
                .otp(otp)
                .purpose(purpose)
                .expiresAt(LocalDateTime.now().plusMinutes(OTP_VALIDITY_MINUTES))
                .used(false)
                .attempts(0)
                .resendCount(0)
                .createdAt(LocalDateTime.now())
                .lastSentAt(LocalDateTime.now())
                .ipAddress(ipAddress)  // NEW: Track IP
                .build();

        otpRepository.save(emailOtp);

        // 6️⃣ Send email (sync recommended)
        try {
//            emailService.sendOtp(email, otp, purpose);
            log.info("OTP is for testing {}",otp ); // remove comments in production
            log.info("OTP sent successfully to: {} for purpose: {}", email, purpose);
        } catch (Exception e) {
            log.error("Failed to send OTP to: {}", email, e);
            throw new IllegalStateException("Failed to send OTP. Please try again.");
        }
    }

    private String generateSecureOtp() {
        SecureRandom secureRandom = new SecureRandom();
        return String.format("%06d", secureRandom.nextInt(1_000_000));
    }

    @Transactional
    public void resendOtp(String email, OtpPurpose purpose, String ipAddress) {

        // IP rate limiting
        rateLimitService.checkIpRateLimit(ipAddress);

        EmailOtp otp = otpRepository
                .findTopByEmailAndPurposeOrderByCreatedAtDesc(email, purpose)
                .orElseThrow(() -> new IllegalArgumentException("No OTP found. Please request a new one."));

        // 1️⃣ Already used
        if (otp.isUsed()) {
            throw new IllegalArgumentException("OTP already verified. Please request a new one.");
        }

        // 2️⃣ Locked due to brute force
        if (otp.getLockedUntil() != null &&
                otp.getLockedUntil().isAfter(LocalDateTime.now())) {

            long minutesRemaining = java.time.Duration
                    .between(LocalDateTime.now(), otp.getLockedUntil())
                    .toMinutes();

            throw new IllegalArgumentException(
                    String.format("Account temporarily locked. Try again in %d minutes.", minutesRemaining)
            );
        }

        // 3️⃣ Resend limit
        if (otp.getResendCount() >= MAX_RESEND_COUNT) {
            throw new IllegalArgumentException("Resend limit reached. Please request a new OTP.");
        }

        // 4️⃣ Cooldown check
        if (otp.getLastSentAt() != null &&
                otp.getLastSentAt().isAfter(LocalDateTime.now().minusSeconds(RESEND_COOLDOWN_SECONDS))) {

            long secondsRemaining = RESEND_COOLDOWN_SECONDS -
                    java.time.Duration.between(otp.getLastSentAt(), LocalDateTime.now()).getSeconds();

            throw new IllegalArgumentException(
                    String.format("Please wait %d seconds before resending", secondsRemaining)
            );
        }

        // 5️⃣ Regenerate OTP
        otp.setOtp(generateSecureOtp());
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(OTP_VALIDITY_MINUTES));
        otp.setResendCount(otp.getResendCount() + 1);
        otp.setAttempts(0);
        otp.setLastSentAt(LocalDateTime.now());

        otpRepository.save(otp);

        try {
            emailService.sendOtp(email, otp.getOtp(), purpose);
            log.info("OTP resent to: {}", email);
        } catch (Exception e) {
            log.error("Failed to resend OTP to: {}", email, e);
            throw new IllegalStateException("Failed to resend OTP. Please try again.");
        }
    }

    @Transactional
    public void validateOtp(String email, String otp, OtpPurpose purpose) {
        EmailOtp emailOtp = otpRepository
                .findTopByEmailAndPurposeAndUsedFalseOrderByExpiresAtDesc(email, purpose)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired OTP"));

        // 1️⃣ Check if locked
        if (emailOtp.getLockedUntil() != null &&
                emailOtp.getLockedUntil().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Too many failed attempts. Account temporarily locked.");
        }

        // 2️⃣ Check expiry
        if (emailOtp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("OTP expired. Please request a new one.");
        }

        // 3️⃣ Check attempts
        if (emailOtp.getAttempts() >= MAX_ATTEMPTS) {
            emailOtp.setLockedUntil(LocalDateTime.now().plusMinutes(LOCKOUT_MINUTES));
            otpRepository.save(emailOtp);
            log.warn("Max attempts reached for email: {}", email);
            throw new IllegalArgumentException("Too many failed attempts. Try again in 15 minutes.");
        }

        // 4️⃣ Validate OTP
        if (!emailOtp.getOtp().equals(otp)) {
            emailOtp.setAttempts(emailOtp.getAttempts() + 1);
            otpRepository.save(emailOtp);

            int remainingAttempts = MAX_ATTEMPTS - emailOtp.getAttempts();
            throw new IllegalArgumentException(
                    String.format("Invalid OTP. %d attempts remaining.", remainingAttempts)
            );
        }

        // 5️⃣ Mark as used
        emailOtp.setUsed(true);
        emailOtp.setVerifiedAt(LocalDateTime.now());
        otpRepository.save(emailOtp);

        log.info("OTP verified successfully for: {}", email);
    }

    // Cleanup expired OTPs (run via scheduled job)
    @Transactional
    public void cleanupExpiredOtps() {
        otpRepository.deleteByExpiresAtBeforeAndUsedTrue(
                LocalDateTime.now().minusDays(7)
        );
    }
}