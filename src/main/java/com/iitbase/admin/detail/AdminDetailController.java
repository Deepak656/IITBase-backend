package com.iitbase.admin.detail;

import com.iitbase.admin.detail.dto.*;
import com.iitbase.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/detail")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminDetailController {

    private final AdminDetailService detailService;

    @GetMapping("/community-job/{id}")
    public ResponseEntity<ApiResponse<CommunityJobDetailResponse>> communityJob(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                detailService.getCommunityJobDetail(id)));
    }

    @GetMapping("/recruiter-job/{id}")
    public ResponseEntity<ApiResponse<RecruiterJobDetailResponse>> recruiterJob(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                detailService.getRecruiterJobDetail(id)));
    }

    @GetMapping("/company/{id}")
    public ResponseEntity<ApiResponse<AdminCompanyDetailResponse>> company(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                detailService.getCompanyDetail(id)));
    }

    @GetMapping("/recruiter/{id}")
    public ResponseEntity<ApiResponse<AdminRecruiterDetailResponse>> recruiter(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                detailService.getRecruiterDetail(id)));
    }

    @GetMapping("/jobseeker/{id}")
    public ResponseEntity<ApiResponse<AdminJobseekerDetailResponse>> jobseeker(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                detailService.getJobseekerDetail(id)));
    }
}