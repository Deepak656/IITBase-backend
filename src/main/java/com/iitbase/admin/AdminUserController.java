package com.iitbase.admin;

import com.iitbase.auth.TokenService;
import com.iitbase.common.ApiResponse;
import com.iitbase.user.User;
import com.iitbase.user.UserRole;
import com.iitbase.user.UserRepository;
import com.iitbase.user.UserService;
import com.iitbase.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserRepository userRepository;
    private final UserService    userService;
    private final TokenService   tokenService;    // ← inject properly

    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0")         int page,
            @RequestParam(defaultValue = "20")        int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC")      String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("ASC")
                ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size,
                Sort.by(sortDirection, sortBy));
        Page<User> users = userRepository.findAll(pageable);

        return ResponseEntity.ok(ApiResponse.success(users.map(user ->
                UserResponse.builder()
                        .email(user.getEmail())
                        .role(user.getRole().name())
                        .activeSessions(userService.getActiveSessionCount(user.getEmail()))
                        .build()
        )));
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getUsersByRole(
            @PathVariable String role) {

        UserRole userRole;
        try {
            userRole = UserRole.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + role);
        }

        List<UserResponse> response = userRepository.findByRole(userRole)
                .stream()
                .map(user -> UserResponse.builder()
                        .email(user.getEmail())
                        .role(user.getRole().name())
                        .activeSessions(userService.getActiveSessionCount(user.getEmail()))
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserStats() {
        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "totalUsers", userRepository.count(),
                "admins",     userRepository.countByRole(UserRole.ADMIN),
                "recruiters", userRepository.countByRole(UserRole.RECRUITER),
                "jobSeekers", userRepository.countByRole(UserRole.JOB_SEEKER)
        )));
    }

    @GetMapping("/{email}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByEmail(
            @PathVariable String email) {
        UserResponse user = userService.getUserProfile(email);
        user.setActiveSessions(userService.getActiveSessionCount(email));
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    // ── Fixed: inject TokenService via constructor, not manual instantiation ──
    @PostMapping("/{email}/force-logout")
    public ResponseEntity<ApiResponse<Void>> forceLogout(@PathVariable String email) {
        userService.findByEmail(email);             // verify exists
        tokenService.invalidateAllUserTokens(email);
        log.warn("Admin force-logged out user: {}", email);
        return ResponseEntity.ok(ApiResponse.success(null,
                "All sessions invalidated for: " + email));
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String email) {
        userService.deleteAccount(email);
        log.warn("Admin deleted user: {}", email);
        return ResponseEntity.ok(ApiResponse.success(null, "User deleted"));
    }
}