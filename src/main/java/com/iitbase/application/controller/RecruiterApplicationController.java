package com.iitbase.application.controller;

import com.iitbase.application.dto.request.UpdateApplicationStatusRequest;
import com.iitbase.application.dto.response.ApplicationDetailResponse;
import com.iitbase.application.service.ApplicationService;
import com.iitbase.common.ApiResponse;
import com.iitbase.recruiter.repository.RecruiterRepository;
import com.iitbase.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/recruiter/applications")
@RequiredArgsConstructor
public class RecruiterApplicationController {

    private final ApplicationService applicationService;
    private final RecruiterRepository recruiterRepository;

    // View all applicants for a specific job
    @GetMapping("/jobs/{jobId}")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getApplicants(
            @PathVariable Long jobId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal User user) {

        Long recruiterId = resolveRecruiterId(user.getId());

        return ResponseEntity.ok(ApiResponse.success(
                applicationService.getApplicantsForJob(recruiterId, jobId, page, size)
        ));
    }

    // View one application in detail
    @GetMapping("/{applicationId}")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getApplicationDetail(
            @PathVariable Long applicationId,
            @AuthenticationPrincipal User user) {

        Long recruiterId = resolveRecruiterId(user.getId());

        return ResponseEntity.ok(ApiResponse.success(
                applicationService.getApplicationDetail(recruiterId, applicationId)
        ));
    }

    // Move pipeline — APPLIED → SCREENING → INTERVIEW → OFFER → HIRED / REJECTED
    @PatchMapping("/{applicationId}/status")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<ApiResponse<ApplicationDetailResponse>> updateStatus(
            @PathVariable Long applicationId,
            @Valid @RequestBody UpdateApplicationStatusRequest request,
            @AuthenticationPrincipal User user) {

        Long recruiterId = resolveRecruiterId(user.getId());

        return ResponseEntity.ok(ApiResponse.success(
                applicationService.updateStatus(recruiterId, applicationId, request),
                "Application status updated"
        ));
    }

    // Update private recruiter notes on a candidate
    @PatchMapping("/{applicationId}/notes")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<ApiResponse<ApplicationDetailResponse>> updateNotes(
            @PathVariable Long applicationId,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal User user) {

        Long recruiterId = resolveRecruiterId(user.getId());
        String notes = body.get("notes");

        return ResponseEntity.ok(ApiResponse.success(
                applicationService.updateNotes(recruiterId, applicationId, notes),
                "Notes updated"
        ));
    }

    // ── Private helper ────────────────────────────────────────────────────────
    private Long resolveRecruiterId(Long userId) {
        return recruiterRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException(
                        "Recruiter profile not found for userId: " + userId
                ))
                .getId();
    }
}