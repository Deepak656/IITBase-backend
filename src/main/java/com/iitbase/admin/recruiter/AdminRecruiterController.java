package com.iitbase.admin.recruiter;

import com.iitbase.admin.recruiter.dto.AdminRecruiterResponse;
import com.iitbase.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/recruiters")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminRecruiterController {

    private final AdminRecruiterService adminRecruiterService;

    // List — GET /api/admin/recruiters?page=0&size=20
    @GetMapping
    public ResponseEntity<ApiResponse<Page<AdminRecruiterResponse>>> getAllRecruiters(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                adminRecruiterService.getAllRecruiters(page, size)));
    }

    // By company — GET /api/admin/recruiters/company/{companyId}
    @GetMapping("/company/{companyId}")
    public ResponseEntity<ApiResponse<Page<AdminRecruiterResponse>>> getByCompany(
            @PathVariable Long companyId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                adminRecruiterService.getRecruitersByCompany(companyId, page, size)));
    }

    // Detail — GET /api/admin/recruiters/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminRecruiterResponse>> getRecruiter(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                adminRecruiterService.getRecruiter(id)));
    }

    // Suspend — POST /api/admin/recruiters/{id}/suspend
    @PostMapping("/{id}/suspend")
    public ResponseEntity<ApiResponse<AdminRecruiterResponse>> suspendRecruiter(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                adminRecruiterService.suspendRecruiter(id),
                "Recruiter suspended — all active sessions invalidated"));
    }

    // Unsuspend — POST /api/admin/recruiters/{id}/unsuspend
    @PostMapping("/{id}/unsuspend")
    public ResponseEntity<ApiResponse<AdminRecruiterResponse>> unsuspendRecruiter(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                adminRecruiterService.unsuspendRecruiter(id),
                "Recruiter unsuspended — they can log in again"));
    }
}