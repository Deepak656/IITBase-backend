package com.iitbase.admin.staff;

import com.iitbase.admin.staff.dto.AcceptStaffInviteRequest;
import com.iitbase.admin.staff.dto.StaffInviteRequest;
import com.iitbase.admin.staff.dto.StaffInviteResponse;
import com.iitbase.auth.dto.AuthResponse;
import com.iitbase.common.ApiResponse;
import com.iitbase.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/staff")
@RequiredArgsConstructor
public class StaffInviteController {

    private final StaffInviteService staffInviteService;

    /**
     * POST /api/admin/staff/invite
     * Send an invite to a new staff member.
     * Requires: ADMIN role
     */
    @PostMapping("/invite")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<StaffInviteResponse>> sendInvite(
            @Valid @RequestBody StaffInviteRequest request,
            @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.ok(ApiResponse.success(
                staffInviteService.sendInvite(
                        currentUser.getEmail(),
                        currentUser.getId(),
                        request),
                "Invite sent to " + request.getEmail()));
    }

    /**
     * GET /api/admin/staff/invites
     * List all staff invites (any status).
     * Requires: ADMIN role
     */
    @GetMapping("/invites")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<StaffInviteResponse>>> listInvites(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(
                ApiResponse.success(staffInviteService.listInvites(page, size)));
    }

    /**
     * DELETE /api/admin/staff/invites/{id}
     * Revoke a pending invite.
     * Requires: ADMIN role
     */
    @DeleteMapping("/invites/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<StaffInviteResponse>> revokeInvite(
            @PathVariable Long id) {

        return ResponseEntity.ok(ApiResponse.success(
                staffInviteService.revokeInvite(id),
                "Invite revoked"));
    }

    /**
     * GET /api/admin/staff/invite/validate?token=xxx
     * Check if a token is valid before showing the accept form.
     * PUBLIC — no auth required (recipient may not be logged in yet)
     */
    @GetMapping("/invite/validate")
    public ResponseEntity<ApiResponse<StaffInviteResponse>> validateToken(
            @RequestParam String token) {

        return ResponseEntity.ok(
                ApiResponse.success(staffInviteService.validateToken(token)));
    }

    /**
     * POST /api/admin/staff/invite/accept
     * Accept an invite. Creates account if needed, promotes to ADMIN.
     * PUBLIC — no auth required
     */
    @PostMapping("/invite/accept")
    public ResponseEntity<ApiResponse<AuthResponse>> acceptInvite(
            @Valid @RequestBody AcceptStaffInviteRequest request) {

        return ResponseEntity.ok(ApiResponse.success(
                staffInviteService.acceptInvite(request),
                "Welcome to IITBase staff"));
    }
}