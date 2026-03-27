package com.iitbase.admin.jobseeker;

import com.iitbase.admin.jobseeker.dto.AdminJobseekerResponse;
import com.iitbase.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/jobseekers")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminJobseekerController {

    private final AdminJobseekerService adminJobseekerService;

    /**
     * GET /api/admin/jobseekers?page=0&size=20&search=rahul&verified=false
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<AdminJobseekerResponse>>> getAll(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false)    String search,
            @RequestParam(required = false)    Boolean verified) {

        return ResponseEntity.ok(ApiResponse.success(
                adminJobseekerService.getAll(page, size, search, verified)));
    }

    /**
     * GET /api/admin/jobseekers/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminJobseekerResponse>> getOne(
            @PathVariable Long id) {

        return ResponseEntity.ok(ApiResponse.success(
                adminJobseekerService.getOne(id)));
    }

    /**
     * POST /api/admin/jobseekers/{id}/verify
     * Verifies the profile and triggers the welcome email.
     */
    @PostMapping("/{id}/verify")
    public ResponseEntity<ApiResponse<AdminJobseekerResponse>> verify(
            @PathVariable Long id) {

        return ResponseEntity.ok(ApiResponse.success(
                adminJobseekerService.verify(id),
                "Profile verified — welcome email sent"));
    }

    /**
     * POST /api/admin/jobseekers/{id}/unverify
     */
    @PostMapping("/{id}/unverify")
    public ResponseEntity<ApiResponse<AdminJobseekerResponse>> unverify(
            @PathVariable Long id) {

        return ResponseEntity.ok(ApiResponse.success(
                adminJobseekerService.unverify(id),
                "Profile unverified"));
    }
}