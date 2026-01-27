package com.iitbase.user;

import com.iitbase.common.ApiResponse;
import com.iitbase.user.dto.ChangePasswordRequest;
import com.iitbase.user.dto.UpdateProfileRequest;
import com.iitbase.user.dto.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Get current user profile
     * GET /api/user/me
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        UserResponse user = userService.getUserProfile(email);

        // Add active session count
        long activeSessions = userService.getActiveSessionCount(email);
        user.setActiveSessions(activeSessions);

        return ResponseEntity.ok(ApiResponse.success(user));
    }

    /**
     * Update user profile
     * PUT /api/user/profile
     */
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            Authentication authentication) {

        String email = authentication.getName();
        UserResponse updatedUser = userService.updateProfile(email, request);

        log.info("Profile updated for user: {}", email);
        return ResponseEntity.ok(ApiResponse.success(updatedUser, "Profile updated successfully"));
    }

    /**
     * Change password
     * POST /api/user/change-password
     */
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        // Validate password confirmation
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("New passwords do not match");
        }

        userService.changePassword(email, request.getCurrentPassword(), request.getNewPassword());

        log.info("Password changed for user: {}", email);
        return ResponseEntity.ok(ApiResponse.success(null,
                "Password changed successfully. All active sessions have been logged out."));
    }

    /**
     * Get active session count
     * GET /api/user/sessions
     */
    @GetMapping("/sessions")
    public ResponseEntity<ApiResponse<Long>> getActiveSessions(Authentication authentication) {
        String email = authentication.getName();
        long sessionCount = userService.getActiveSessionCount(email);

        return ResponseEntity.ok(ApiResponse.success(sessionCount,
                "Active sessions: " + sessionCount));
    }

    /**
     * Delete user account
     * DELETE /api/user/account
     */
    @DeleteMapping("/account")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(
            @RequestParam String confirmEmail,
            Authentication authentication) {

        String email = authentication.getName();

        // Require email confirmation for safety
        if (!email.equals(confirmEmail)) {
            throw new IllegalArgumentException("Email confirmation does not match");
        }

        userService.deleteAccount(email);

        log.warn("Account deleted for user: {}", email);
        return ResponseEntity.ok(ApiResponse.success(null, "Account deleted successfully"));
    }
}