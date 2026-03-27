package com.iitbase.admin.job;

import com.iitbase.common.ApiResponse;
import com.iitbase.community.dto.JobResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/jobs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminJobController {

    private final AdminJobService adminService;

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<JobResponse>>> getPendingJobs() {
        List<JobResponse> jobs = adminService.getPendingJobs();
        return ResponseEntity.ok(ApiResponse.success(jobs));
    }

    @GetMapping("/reported")
    public ResponseEntity<ApiResponse<List<JobResponse>>> getReportedJobs() {
        List<JobResponse> jobs = adminService.getReportedJobs();
        return ResponseEntity.ok(ApiResponse.success(jobs));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<Void>> approveJob(@PathVariable Long id) {
        adminService.approveJob(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Job approved"));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<Void>> rejectJob(@PathVariable Long id) {
        adminService.rejectJob(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Job rejected"));
    }

    @PostMapping("/{id}/mark-expired")
    public ResponseEntity<ApiResponse<Void>> markExpired(@PathVariable Long id) {
        adminService.markExpired(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Job marked as expired"));
    }
}