package com.iitbase.recruiter.controller;

import com.iitbase.common.ApiResponse;
import com.iitbase.recruiter.dto.request.*;
import com.iitbase.recruiter.dto.response.*;
import com.iitbase.recruiter.service.TeamService;
import com.iitbase.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/recruiter/team")
@RequiredArgsConstructor
@PreAuthorize("hasRole('RECRUITER')")
public class TeamController {

    private final TeamService teamService;

    // ── Team members ──────────────────────────────────────────────────────────

    /** GET /api/v1/recruiter/team — list all members of my company */
    @GetMapping
    public ResponseEntity<ApiResponse<List<TeamMemberResponse>>> getTeam(
            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(
                ApiResponse.success(teamService.getTeamMembers(user.getId())));
    }

    /** PATCH /api/v1/recruiter/team/{id}/role — promote or demote a member */
    @PatchMapping("/{recruiterId}/role")
    public ResponseEntity<ApiResponse<TeamMemberResponse>> updateRole(
            @PathVariable Long recruiterId,
            @Valid @RequestBody UpdateTeamMemberRoleRequest request,
            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(ApiResponse.success(
                teamService.updateMemberRole(user.getId(), recruiterId, request.getRole()),
                "Role updated successfully"));
    }

    /** DELETE /api/v1/recruiter/team/{id} — remove a member */
    @DeleteMapping("/{recruiterId}")
    public ResponseEntity<ApiResponse<Void>> removeMember(
            @PathVariable Long recruiterId,
            @AuthenticationPrincipal User user) {

        teamService.removeTeamMember(user.getId(), recruiterId);
        return ResponseEntity.ok(ApiResponse.success(null, "Member removed"));
    }

    // ── Invites ───────────────────────────────────────────────────────────────

    /** POST /api/v1/recruiter/team/invites — send invite by email */
    @PostMapping("/invites")
    public ResponseEntity<ApiResponse<RecruiterInviteResponse>> invite(
            @Valid @RequestBody InviteTeamMemberRequest request,
            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(ApiResponse.success(
                teamService.inviteTeamMember(user.getId(), request),
                "Invite sent successfully"));
    }

    /** GET /api/v1/recruiter/team/invites — list pending invites */
    @GetMapping("/invites")
    public ResponseEntity<ApiResponse<Page<RecruiterInviteResponse>>> getPendingInvites(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(
                ApiResponse.success(teamService.getPendingInvites(user.getId(), page, size)));
    }

    /** DELETE /api/v1/recruiter/team/invites/{id} — revoke a pending invite */
    @DeleteMapping("/invites/{inviteId}")
    public ResponseEntity<ApiResponse<Void>> revokeInvite(
            @PathVariable Long inviteId,
            @AuthenticationPrincipal User user) {

        teamService.revokeInvite(user.getId(), inviteId);
        return ResponseEntity.ok(ApiResponse.success(null, "Invite revoked"));
    }

    // ── Join requests ─────────────────────────────────────────────────────────

    /** GET /api/v1/recruiter/team/join-requests — pending requests to join */
    @GetMapping("/join-requests")
    public ResponseEntity<ApiResponse<Page<JoinRequestResponse>>> getJoinRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(ApiResponse.success(
                teamService.getPendingJoinRequests(user.getId(), page, size)));
    }

    /** POST /api/v1/recruiter/team/join-requests/{id}/approve */
    @PostMapping("/join-requests/{requestId}/approve")
    public ResponseEntity<ApiResponse<JoinRequestResponse>> approveJoinRequest(
            @PathVariable Long requestId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String designation,
            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(ApiResponse.success(
                teamService.approveJoinRequest(user.getId(), requestId, name, designation),
                "Join request approved"));
    }

    /** POST /api/v1/recruiter/team/join-requests/{id}/reject */
    @PostMapping("/join-requests/{requestId}/reject")
    public ResponseEntity<ApiResponse<JoinRequestResponse>> rejectJoinRequest(
            @PathVariable Long requestId,
            @RequestBody ReviewJoinRequestRequest request,
            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(ApiResponse.success(
                teamService.rejectJoinRequest(
                        user.getId(), requestId, request.getRejectionReason()),
                "Join request rejected"));
    }
}