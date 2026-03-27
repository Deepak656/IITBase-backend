package com.iitbase.scheduler;

import com.iitbase.email.otp.OtpService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class OtpCleanupService {

    private final OtpService otpService;

    public OtpCleanupService(OtpService otpService) {
        this.otpService = otpService;
    }

    @Scheduled(cron = "0 0 2 * * *") // Runs at 2 AM daily
    public void cleanupOldOtps() {
        otpService.cleanupExpiredOtps();
    }
}

