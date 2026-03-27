package com.iitbase.email;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Controls which categories of email are actually sent.
 *
 * Set in application.yml or via Railway env vars:
 *
 *   EMAIL_ENABLED=false          → kills ALL email (master switch)
 *   EMAIL_OTP_ENABLED=false      → suppresses OTP emails only
 *   EMAIL_EVENTS_ENABLED=false   → suppresses welcome/invite/notification emails
 *
 * All three default to true so production works without any env vars set.
 * For local dev, set EMAIL_ENABLED=false in .env.local and nothing sends.
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "email")
public class EmailToggleConfig {

    /**
     * Master switch — if false, no email of any kind is sent.
     * Overrides the two granular switches below.
     */
    private boolean enabled = true;

    /**
     * Controls OTP emails (signup, reset password, change email).
     * Useful to disable during load testing or UI development
     * when you don't want real OTPs fired but still want event emails.
     */
    private boolean otpEnabled = true;

    /**
     * Controls all event-driven emails:
     * welcome, team invite, join request, staff invite, company verified, etc.
     * Safe to disable during backend integration testing.
     */
    private boolean eventsEnabled = true;

    // ── Convenience helpers ───────────────────────────────────────────────

    public boolean canSendOtp() {
        return enabled && otpEnabled;
    }

    public boolean canSendEvents() {
        return enabled && eventsEnabled;
    }
}
