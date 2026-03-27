package com.iitbase.config;

import com.iitbase.user.User;
import com.iitbase.user.UserRepository;
import com.iitbase.user.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Seeds the first IITBase admin account on startup.
 *
 * Set env var:  ADMIN_SEED_EMAIL=founder@iitbase.com
 *
 * On first boot:
 *   - Creates a User with role=ADMIN and a random unusable password
 *   - No JobseekerProfile created (bypasses the signup flow entirely)
 *   - Logs a clear message so you know it ran
 *
 * After that:
 *   - Go to /reset-password and request an OTP for your email
 *   - Set a real password
 *   - Done — you can now log in at /login
 *
 * Subsequent boots:
 *   - If the email already exists, does nothing (idempotent)
 *   - Safe to leave the env var set permanently
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminSeeder implements ApplicationRunner {

    private final UserRepository  userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.seed-email:}")
    private String seedEmail;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (seedEmail == null || seedEmail.isBlank()) {
            // Env var not set — nothing to do
            return;
        }

        String email = seedEmail.toLowerCase().trim();

        if (userRepository.existsByEmail(email)) {
            // Already exists — check if they need promotion
            userRepository.findByEmail(email).ifPresent(existing -> {
                if (existing.getRole() != UserRole.ADMIN) {
                    existing.setRole(UserRole.ADMIN);
                    userRepository.save(existing);
                    log.info("AdminSeeder: promoted existing user {} to ADMIN", email);
                } else {
                    log.debug("AdminSeeder: {} is already ADMIN, skipping", email);
                }
            });
            return;
        }

        // Create a bare admin user with a random unusable password.
        // The password is a UUID hash — practically impossible to guess.
        // The user MUST go through /reset-password to set a real one.
        String unusablePassword = passwordEncoder.encode(UUID.randomUUID().toString());

        User admin = User.builder()
                .email(email)
                .password(unusablePassword)
                .role(UserRole.ADMIN)
                .build();

        userRepository.save(admin);

        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("AdminSeeder: created admin account for {}", email);
        log.info("Next step: go to /reset-password and request an OTP");
        log.info("           to set your password, then log in at /login");
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
}