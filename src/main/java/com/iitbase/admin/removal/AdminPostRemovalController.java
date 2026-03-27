package com.iitbase.admin.removal;

import com.iitbase.common.ApiResponse;
import com.iitbase.removal.JobRemovalRequest;
import com.iitbase.removal.RemovalStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/removals")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminPostRemovalController {

    private final AdminPostRemovalService adminPostRemovalService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<JobRemovalRequest>>> getAllRequests(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false)    RemovalStatus status) {
        return ResponseEntity.ok(ApiResponse.success(
                adminPostRemovalService.getAllRequests(page, size, status)));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<JobRemovalRequest>> approveRemoval(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                adminPostRemovalService.approveRemoval(id),
                "Removal approved — job removed from feed"));
    }

    @PostMapping("/{id}/deny")
    public ResponseEntity<ApiResponse<JobRemovalRequest>> denyRemoval(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                adminPostRemovalService.denyRemoval(id),
                "Removal request denied"));
    }
}