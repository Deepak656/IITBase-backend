package com.iitbase.user;

import com.iitbase.auth.TokenService;
import com.iitbase.user.dto.UpdateProfileRequest;
import com.iitbase.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Update user password (for password reset)
     * Invalidates all active sessions
     */
    @Transactional
    public void updatePassword(String email, String newPassword) {
        User user = findByEmail(email);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Invalidate all active sessions for security
        tokenService.invalidateAllUserTokens(email);

        log.info("Password updated for user: {}", email);
    }

    /**
     * Update user profile
     */
    @Transactional
    public UserResponse updateProfile(String email, UpdateProfileRequest request) {
        User user = findByEmail(email);

        if (request.getCollege() != null) {
            user.setCollege(request.getCollege());
        }
        if (request.getGraduationYear() != null) {
            user.setGraduationYear(request.getGraduationYear());
        }

        userRepository.save(user);
        log.info("Profile updated for user: {}", email);

        return mapToResponse(user);
    }

    /**
     * Change password (requires old password verification)
     */
    @Transactional
    public void changePassword(String email, String oldPassword, String newPassword) {
        User user = findByEmail(email);

        // Verify old password
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Validate new password
        if (newPassword.length() < 8) {
            throw new IllegalArgumentException("New password must be at least 8 characters");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Invalidate all sessions except current (optional - for now invalidate all)
        tokenService.invalidateAllUserTokens(email);

        log.info("Password changed for user: {}", email);
    }

    /**
     * Get user profile
     */
    public UserResponse getUserProfile(String email) {
        User user = findByEmail(email);
        return mapToResponse(user);
    }

    /**
     * Get active session count
     */
    public long getActiveSessionCount(String email) {
        return tokenService.getUserActiveSessionCount(email);
    }

    /**
     * Delete user account
     */
    @Transactional
    public void deleteAccount(String email) {
        User user = findByEmail(email);

        // Invalidate all sessions
        tokenService.invalidateAllUserTokens(email);

        // Delete user
        userRepository.delete(user);

        log.info("Account deleted for user: {}", email);
    }

    // Helper method
    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .email(user.getEmail())
                .role(user.getRole().name())
                .college(user.getCollege())
                .graduationYear(user.getGraduationYear())
                .build();
    }
}