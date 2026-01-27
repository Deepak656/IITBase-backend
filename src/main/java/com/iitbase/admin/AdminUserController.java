package com.iitbase.admin;

import com.iitbase.common.ApiResponse;
import com.iitbase.user.User;
import com.iitbase.user.UserRole;
import com.iitbase.user.dto.UserResponse;
import com.iitbase.user.UserRepository;
import com.iitbase.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final UserService userService;

    /**
     * Get all users (paginated)
     * GET /api/admin/users
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("ASC")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<User> users = userRepository.findAll(pageable);

        Page<UserResponse> response = users.map(user -> UserResponse.builder()
                .email(user.getEmail())
                .role(user.getRole().name())
                .college(user.getCollege())
                .graduationYear(user.getGraduationYear())
                .activeSessions(userService.getActiveSessionCount(user.getEmail()))
                .build());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get users by role
     * GET /api/admin/users/role/{role}
     */
    @GetMapping("/role/{role}")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getUsersByRole(
            @PathVariable String role) {

        UserRole userRole;
        try {
            userRole = UserRole.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + role);
        }

        List<User> users = userRepository.findByRole(userRole);
        List<UserResponse> response = users.stream()
                .map(user -> UserResponse.builder()
                        .email(user.getEmail())
                        .role(user.getRole().name())
                        .college(user.getCollege())
                        .graduationYear(user.getGraduationYear())
                        .activeSessions(userService.getActiveSessionCount(user.getEmail()))
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get user statistics
     * GET /api/admin/users/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserStats() {
        long totalUsers = userRepository.count();
        long admins = userRepository.countByRole(UserRole.ADMIN);
        long recruiters = userRepository.countByRole(UserRole.RECRUITER);
        long jobSeekers = userRepository.countByRole(UserRole.JOB_SEEKER);

        Map<String, Object> stats = Map.of(
                "totalUsers", totalUsers,
                "admins", admins,
                "recruiters", recruiters,
                "jobSeekers", jobSeekers
        );

        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    /**
     * Get user by email
     * GET /api/admin/users/{email}
     */
    @GetMapping("/{email}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByEmail(@PathVariable String email) {
        UserResponse user = userService.getUserProfile(email);
        user.setActiveSessions(userService.getActiveSessionCount(email));

        return ResponseEntity.ok(ApiResponse.success(user));
    }

    /**
     * Force logout user (invalidate all sessions)
     * POST /api/admin/users/{email}/force-logout
     */
    @PostMapping("/{email}/force-logout")
    public ResponseEntity<ApiResponse<Void>> forceLogout(@PathVariable String email) {
        userService.findByEmail(email); // Verify user exists

        // Invalidate all user sessions
        com.iitbase.auth.TokenService tokenService =
                new com.iitbase.auth.TokenService(null, null); // This needs proper injection
        tokenService.invalidateAllUserTokens(email);

        log.warn("Admin force-logged out user: {}", email);
        return ResponseEntity.ok(ApiResponse.success(null,
                "All sessions invalidated for user: " + email));
    }

    /**
     * Delete user (admin only)
     * DELETE /api/admin/users/{email}
     */
    @DeleteMapping("/{email}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String email) {
        userService.deleteAccount(email);

        log.warn("Admin deleted user account: {}", email);
        return ResponseEntity.ok(ApiResponse.success(null, "User deleted successfully"));
    }
}
