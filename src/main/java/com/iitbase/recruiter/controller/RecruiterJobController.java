package com.iitbase.recruiter.controller;

import com.iitbase.common.ApiResponse;
import com.iitbase.recruiter.dto.request.PostRecruiterJobRequest;
import com.iitbase.recruiter.dto.request.UpdateJobStatusRequest;
import com.iitbase.recruiter.dto.request.UpdateRecruiterJobRequest;
import com.iitbase.recruiter.dto.response.RecruiterJobResponse;
import com.iitbase.recruiter.service.RecruiterJobService;
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
@RequestMapping("/api/v1/recruiter/jobs")
@RequiredArgsConstructor
public class RecruiterJobController {

    private final RecruiterJobService recruiterJobService;

    @PostMapping
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<ApiResponse<RecruiterJobResponse>> postJob(
            @Valid @RequestBody PostRecruiterJobRequest request,
            @AuthenticationPrincipal User user) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        recruiterJobService.postJob(user.getId(), request),
                        "Job posted successfully"
                ));
    }

    @GetMapping("/my-listings")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMyListings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(ApiResponse.success(
                recruiterJobService.getMyJobs(user.getId(), page, size)
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RecruiterJobResponse>> getJob(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(recruiterJobService.getById(id))
        );
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<ApiResponse<RecruiterJobResponse>> updateJob(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRecruiterJobRequest request,
            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(ApiResponse.success(
                recruiterJobService.updateJob(user.getId(), id, request),
                "Job updated successfully"
        ));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<ApiResponse<Void>> removeJob(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        recruiterJobService.removeJob(user.getId(), id);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Job removed successfully")
        );
    }
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<ApiResponse<RecruiterJobResponse>> updateJobStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateJobStatusRequest request,
            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(ApiResponse.success(
                recruiterJobService.updateJobStatus(user.getId(), id, request.getStatus()),
                "Job status updated successfully"
        ));
    }
}