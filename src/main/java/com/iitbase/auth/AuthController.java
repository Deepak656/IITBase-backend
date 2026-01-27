package com.iitbase.auth;

import com.iitbase.auth.dto.AuthResponse;
import com.iitbase.auth.dto.LoginRequest;
import com.iitbase.auth.dto.SignupRequest;
import com.iitbase.common.ApiResponse;
import com.iitbase.email.OtpPurpose;
import com.iitbase.email.OtpService;
import com.iitbase.email.ResendOtpRequest;
import com.iitbase.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final OtpService otpService;

    @PostMapping("/signup/request-otp")
    public ResponseEntity<ApiResponse<Void>> requestSignupOtp(
            @RequestParam String email,
            HttpServletRequest request) {

        if (userService.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered");
        }

        String ipAddress = getClientIp(request);
        otpService.generateAndSendOtp(email, OtpPurpose.SIGNUP, ipAddress);

        return ResponseEntity.ok(ApiResponse.success(null, "OTP sent successfully"));
    }

    @PostMapping("/signup/verify-otp")
    public ResponseEntity<ApiResponse<AuthResponse>> verifySignupOtp(
            @Valid @RequestBody SignupRequest signupRequest,
            @RequestParam String otp) {

        otpService.validateOtp(signupRequest.getEmail(), otp, OtpPurpose.SIGNUP);
        AuthResponse response = authService.signup(signupRequest);

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
}