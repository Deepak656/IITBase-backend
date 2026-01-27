package com.iitbase.auth;

import com.iitbase.auth.dto.AuthResponse;
import com.iitbase.auth.dto.LoginRequest;
import com.iitbase.auth.dto.SignupRequest;
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
    public AuthResponse signup(SignupRequest request) {
        log.info("Signup attempt for email: {}", request.getEmail());

        if (userService.existsByEmail(request.getEmail())) {
            log.warn("Signup failed - email already exists: {}", request.getEmail());
            throw new IllegalArgumentException("Email already exists");
        }

        if (request.getPassword().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }

        try {
            User user = User.builder()
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(request.getRole())
                    .college(request.getCollege())
                    .graduationYear(request.getGraduationYear())
                    .build();

            userService.save(user);

            // Generate token with JTI and store in Redis
            String[] tokenData = jwtUtil.generateTokenWithJti(user.getEmail(), user.getRole().name());
            String token = tokenData[0];
            String jti = tokenData[1];

            tokenService.storeToken(jti, user.getEmail(), user.getRole().name());

            log.info("User registered successfully: {}", user.getEmail());
            return new AuthResponse(token, user.getRole().name());
        } catch (IllegalArgumentException e){
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
}