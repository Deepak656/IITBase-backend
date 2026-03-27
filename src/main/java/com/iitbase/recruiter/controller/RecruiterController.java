package com.iitbase.recruiter.controller;

import com.iitbase.common.ApiResponse;
import com.iitbase.recruiter.dto.request.CreateRecruiterProfileRequest;
import com.iitbase.recruiter.dto.request.UpdateRecruiterProfileRequest;
import com.iitbase.recruiter.dto.response.RecruiterProfileResponse;
import com.iitbase.recruiter.service.RecruiterService;
import com.iitbase.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/recruiters")
@RequiredArgsConstructor
public class RecruiterController {

    private final RecruiterService recruiterService;

    @PostMapping("/profile")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<ApiResponse<RecruiterProfileResponse>> createProfile(
            @Valid @RequestBody CreateRecruiterProfileRequest request,
            @AuthenticationPrincipal User user) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        recruiterService.createProfile(user.getId(), request),
                        "Recruiter profile created successfully"
                ));
    }

    @GetMapping("/profile/me")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<ApiResponse<RecruiterProfileResponse>> getMyProfile(
            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(
                ApiResponse.success(recruiterService.getMyProfile(user.getId()))
        );
    }

    @PatchMapping("/profile/me")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<ApiResponse<RecruiterProfileResponse>> updateMyProfile(
            @Valid @RequestBody UpdateRecruiterProfileRequest request,
            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(ApiResponse.success(
                recruiterService.updateMyProfile(user.getId(), request),
                "Profile updated successfully"
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RecruiterProfileResponse>> getPublicProfile(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(recruiterService.getPublicProfile(id))
        );
    }
}