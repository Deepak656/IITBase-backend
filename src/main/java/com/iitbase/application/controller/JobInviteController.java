package com.iitbase.application.controller;

import com.iitbase.application.dto.request.SendInviteRequest;
import com.iitbase.application.dto.response.JobInviteResponse;
import com.iitbase.application.service.JobInviteService;
import com.iitbase.common.ApiResponse;
import com.iitbase.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/invites")
@RequiredArgsConstructor
public class JobInviteController {

    private final JobInviteService jobInviteService;

    // Recruiter sends invite to any jobseeker
    @PostMapping
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<ApiResponse<JobInviteResponse>> sendInvite(
            @Valid @RequestBody SendInviteRequest request,
            @AuthenticationPrincipal User user) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        jobInviteService.sendInvite(user.getId(), request),
                        "Invite sent successfully"
                ));
    }

    // Jobseeker views all their received invites
    @GetMapping("/my")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMyInvites(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(ApiResponse.success(
                jobInviteService.getMyInvites(user.getId(), page, size)
        ));
    }

    // Jobseeker declines an invite
    @PatchMapping("/my/{id}/decline")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<ApiResponse<JobInviteResponse>> declineInvite(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(ApiResponse.success(
                jobInviteService.declineInvite(user.getId(), id),
                "Invite declined"
        ));
    }
}