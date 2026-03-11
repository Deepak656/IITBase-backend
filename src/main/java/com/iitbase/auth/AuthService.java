package com.iitbase.auth;

import com.iitbase.auth.dto.AuthResponse;
import com.iitbase.auth.dto.LoginRequest;
import com.iitbase.auth.dto.SignupRequest;
import com.iitbase.auth.dto.SignupResponse;
import com.iitbase.config.JwtUtil;
import com.iitbase.user.User;
import com.iitbase.user.UserRepository;
import com.iitbase.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final TokenService tokenService;

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        log.info("Signup attempt for email: {}", request.getEmail());

        // Validate input first
//        validateSignupRequest(request);

        try {
            // Check if email exists
            if (userService.existsByEmail(request.getEmail())) {
                log.warn("Signup failed - email already exists: {}", request.getEmail());
                throw new IllegalArgumentException("Email already exists");
            }

            // Create and save user
            User user = User.builder()
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(request.getRole())
                    .college(request.getCollege())
                    .graduationYear(request.getGraduationYear())
                    .build();

            User savedUser = userService.save(user);

            // Generate token with JTI and store in Redis
            String[] tokenData = jwtUtil.generateTokenWithJti(
                    savedUser.getEmail(),
                    savedUser.getRole().name()
            );
            String token = tokenData[0];
            String jti = tokenData[1];

            tokenService.storeToken(jti, savedUser.getEmail(), savedUser.getRole().name());

            log.info("User registered successfully: {} with ID: {}", savedUser.getEmail(), savedUser.getId());

            return SignupResponse.builder()
                    .token(token)
                    .role(savedUser.getRole().name())
                    .userId(savedUser.getId())  // FIX: Add userId
                    .build();

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error during signup for email: {}", request.getEmail(), e);
            throw new IllegalStateException("Failed to create account. Please try again.");
        }
    }

    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());

        try {
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> {
                        log.warn("Login failed - user not found: {}", request.getEmail());
                        return new IllegalArgumentException("Invalid email or password");
                    });

            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new IllegalArgumentException("Invalid credentials");
            }

            // Generate token with JTI and store in Redis
            String[] tokenData = jwtUtil.generateTokenWithJti(user.getEmail(), user.getRole().name());
            String token = tokenData[0];
            String jti = tokenData[1];

            tokenService.storeToken(jti, user.getEmail(), user.getRole().name());

            log.info("Login successful for: {}", user.getEmail());
            return new AuthResponse(token, user.getRole().name());
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during login for email: {}", request.getEmail(), e);
            throw new IllegalStateException("Login failed. Please try again.");
        }
    }

    /**
     * Logout - invalidate current token
     */
    public void logout(String jti, String email) {
        tokenService.invalidateToken(jti, email);
        log.info("User logged out: {}", email);
    }

    /**
     * Logout all devices - invalidate all user tokens
     */
    public void logoutAllDevices(String email) {
        tokenService.invalidateAllUserTokens(email);
        log.info("All sessions invalidated for user: {}", email);
    }
    private void validateSignupRequest(SignupRequest request) {
        // Enhanced password validation
        if (request.getPassword().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }

        // Add complexity requirements
        if (!request.getPassword().matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("Password must contain at least one uppercase letter");
        }

        if (!request.getPassword().matches(".*[a-z].*")) {
            throw new IllegalArgumentException("Password must contain at least one lowercase letter");
        }

        if (!request.getPassword().matches(".*\\d.*")) {
            throw new IllegalArgumentException("Password must contain at least one digit");
        }

        // Email format validation
        if (!request.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }

        // Role validation (if you have specific allowed roles)
        if (request.getRole() == null) {
            throw new IllegalArgumentException("Role is required");
        }
    }
}