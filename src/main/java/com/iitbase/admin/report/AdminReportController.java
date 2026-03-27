package com.iitbase.admin.report;

import com.iitbase.common.ApiResponse;
import com.iitbase.report.JobReport;
import com.iitbase.report.JobReportStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminReportController {

    private final AdminReportService adminReportService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<JobReport>>> getAllReports(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false)    JobReportStatus status) {
        return ResponseEntity.ok(ApiResponse.success(
                adminReportService.getAllReports(page, size, status)));
    }

    @PostMapping("/{id}/resolve")
    public ResponseEntity<ApiResponse<JobReport>> resolveReport(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(ApiResponse.success(
                adminReportService.resolveReport(id, body.get("resolution")),
                "Report resolved"));
    }

    @PostMapping("/{id}/dismiss")
    public ResponseEntity<ApiResponse<JobReport>> dismissReport(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                adminReportService.dismissReport(id),
                "Report dismissed"));
    }
}