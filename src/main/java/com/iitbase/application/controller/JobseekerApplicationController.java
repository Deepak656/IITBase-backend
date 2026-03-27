package com.iitbase.application.controller;

import com.iitbase.application.dto.request.ApplyRequest;
import com.iitbase.application.dto.response.ApplicationResponse;
import com.iitbase.application.service.ApplicationService;
import com.iitbase.common.ApiResponse;
import com.iitbase.jobseeker.model.Jobseeker;
import com.iitbase.jobseeker.service.JobseekerProfileService;
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
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
public class JobseekerApplicationController {

    private final ApplicationService applicationService;
    private final JobseekerProfileService jobseekerProfileService;

    @PostMapping
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<ApiResponse<ApplicationResponse>> apply(
            @Valid @RequestBody ApplyRequest request,
            @AuthenticationPrincipal User user) {

        // Resume URL comes from user profile
        // Assumes jobseeker profile has resumeUrl field
        // Wire to your JobseekerProfileService if needed
        String resumeUrl = resolveResumeUrl(user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        applicationService.apply(user.getId(), resumeUrl, request),
                        "Application submitted successfully"
                ));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMyApplications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(ApiResponse.success(
                applicationService.getMyApplications(user.getId(), page, size)
        ));
    }

    @GetMapping("/my/{id}")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMyApplicationDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(ApiResponse.success(
                applicationService.getMyApplicationDetail(user.getId(), id)
        ));
    }

    @PatchMapping("/my/{id}/withdraw")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<ApiResponse<ApplicationResponse>> withdraw(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(ApiResponse.success(
                applicationService.withdraw(user.getId(), id),
                "Application withdrawn"
        ));
    }

    // ── Private helper ────────────────────────────────────────────────────────
    // Wire this to your jobseeker profile service
    // For now reads from User — update when jobseeker profile module is ready
    // Replace the resolveResumeUrl method with:
    private String resolveResumeUrl(User user) {
        // resolveJobseeker uses email — consistent with how jobseeker module works
        Jobseeker profile = jobseekerProfileService
                .resolveJobseeker(user.getEmail());

        if (profile.getResumeUrl() == null || profile.getResumeUrl().isBlank()) {
            throw new IllegalStateException(
                    "Please upload your resume to your profile before applying"
            );
        }
        //If this ever feels wrong, the clean alternative is to pass resumeUrl in the ApplyRequest from the frontend directly — frontend already has it from the profile API. Simpler, zero cross-module dependency:
        return profile.getResumeUrl();
    }
}