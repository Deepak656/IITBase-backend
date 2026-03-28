package com.iitbase.auth;

import com.iitbase.auth.dto.*;
import com.iitbase.common.ApiResponse;
import com.iitbase.email.otp.OtpPurpose;
import com.iitbase.email.otp.OtpService;
import com.iitbase.email.otp.ResendOtpRequest;
import com.iitbase.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final OtpService otpService;
    private final RedisTemplate redisTemplate;

    @PostMapping("/signup/request-otp")
    public ResponseEntity<ApiResponse<Void>> requestSignupOtp(
            @RequestParam String email,
            HttpServletRequest request) {

        if (userService.existsByEmail(email)) {
            throw new IllegalArgumentException("Registration failed or user already exists.");
        }

        String ipAddress = getClientIp(request);
        otpService.generateAndSendOtp(email, OtpPurpose.SIGNUP, ipAddress);

        return ResponseEntity.ok(ApiResponse.success(null, "OTP sent successfully"));
    }
    @Transactional
    @PostMapping("/signup/verify-otp")
    public ResponseEntity<ApiResponse<SignupResponse>> verifySignupOtp(
            @Valid @RequestBody SignupRequest signupRequest,
            @RequestParam String otp) {

        otpService.validateOtp(signupRequest.getEmail(), otp, OtpPurpose.SIGNUP);
        SignupResponse response = authService.signup(signupRequest);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/password/request-otp")
    public ResponseEntity<ApiResponse<Void>> requestResetOtp(
            @RequestParam String email,
            HttpServletRequest request) {

        userService.findByEmail(email);

        String ipAddress = getClientIp(request);
        otpService.generateAndSendOtp(email, OtpPurpose.RESET_PASSWORD, ipAddress);

        return ResponseEntity.ok(ApiResponse.success(null, "Reset OTP sent"));
    }

    @PostMapping("/password/reset")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @RequestParam String email,
            @RequestParam String otp,
            @RequestParam String newPassword) {

        if (newPassword.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }

        otpService.validateOtp(email, otp, OtpPurpose.RESET_PASSWORD);
        userService.updatePassword(email, newPassword);
        // ← ADD THIS: Invalidate all sessions after password change
        authService.logoutAllDevices(email);
        return ResponseEntity.ok(ApiResponse.success(null, "Password reset successful"));
    }
    @PostMapping("/change-email/request-otp")
    public ResponseEntity<ApiResponse<Void>> requestChangeEmailOtp(
            @RequestBody ChangeEmailRequest request,
            Authentication authentication,
            HttpServletRequest httpRequest) {

        String currentEmail = authentication.getName();

        if (userService.existsByEmail(request.getNewEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        String ipAddress = getClientIp(httpRequest);

        otpService.generateAndSendOtp(
                request.getNewEmail(),
                OtpPurpose.CHANGE_EMAIL,
                ipAddress
        );

        return ResponseEntity.ok(ApiResponse.success(null, "OTP sent to new email"));
    }
    @PostMapping("/change-email/verify")
    public ResponseEntity<ApiResponse<Void>> verifyChangeEmail(
            @RequestBody VerifyChangeEmailRequest request,
            Authentication authentication) {

        String currentEmail = authentication.getName();

        otpService.validateOtp(
                request.getNewEmail(),
                request.getOtp(),
                OtpPurpose.CHANGE_EMAIL
        );

        userService.updateEmail(currentEmail, request.getNewEmail());

        // 🔥 CRITICAL: logout all sessions
        authService.logoutAllDevices(currentEmail);

        return ResponseEntity.ok(ApiResponse.success(null, "Email updated successfully"));
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<ApiResponse<Void>> resendOtp(
            @Valid @RequestBody ResendOtpRequest resendRequest,
            HttpServletRequest request) {

        String ipAddress = getClientIp(request);
        otpService.resendOtp(resendRequest.getEmail(), resendRequest.getPurpose(), ipAddress);

        return ResponseEntity.ok(ApiResponse.success(null, "OTP resent successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest loginRequest) {

        AuthResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Logout current session - invalidate current token
     * Requires: Authorization: Bearer <token>
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            HttpServletRequest request,
            Authentication authentication) {

        String jti = (String) request.getAttribute("jti");
        String email = authentication.getName();

        if (jti != null) {
            authService.logout(jti, email);
            return ResponseEntity.ok(ApiResponse.success(null, "Logged out successfully"));
        }

        throw new IllegalStateException("Invalid token - no JTI found");
    }
    /**
     * Logout all devices - invalidate all user tokens
     * Requires: Authorization: Bearer <token>
     */
    @PostMapping("/logout-all")
    public ResponseEntity<ApiResponse<Void>> logoutAll(Authentication authentication) {
        String email = authentication.getName();
        authService.logoutAllDevices(email);
        return ResponseEntity.ok(ApiResponse.success(null, "Logged out from all devices"));
    }
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip != null ? ip.split(",")[0].trim() : "0.0.0.0";
    }

    @PostMapping("/change-email/verify-current/request-otp")
    public ResponseEntity<ApiResponse<Void>> requestVerifyCurrentOtp(
            Authentication authentication,
            HttpServletRequest httpRequest) {

        String currentEmail = authentication.getName();
        String ipAddress = getClientIp(httpRequest);

        otpService.generateAndSendOtp(currentEmail, OtpPurpose.VERIFY_CURRENT_EMAIL, ipAddress);

        return ResponseEntity.ok(ApiResponse.success(null, "OTP sent to current email"));
    }
    @PostMapping("/change-email/verify-current")
    public ResponseEntity<ApiResponse<Void>> verifyCurrentEmail(
            @RequestParam String otp,
            Authentication authentication) {

        String currentEmail = authentication.getName();
        otpService.validateOtp(currentEmail, otp, OtpPurpose.VERIFY_CURRENT_EMAIL);

        return ResponseEntity.ok(ApiResponse.success(null, "Current email verified"));
    }

    @GetMapping("/auth/health/redis")
    public ResponseEntity<String> redisHealth() {
        try {
            redisTemplate.opsForValue().get("ping");
            return ResponseEntity.ok("Redis OK");
        } catch (Exception e) {
            log.error("Redis health check failed", e);
            return ResponseEntity.status(503).body("Redis DOWN: " + e.getMessage());
        }
    }
}