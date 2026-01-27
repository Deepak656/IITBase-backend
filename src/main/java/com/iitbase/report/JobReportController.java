package com.iitbase.report;

import com.iitbase.common.ApiResponse;
import com.iitbase.user.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobReportController {

    private final JobReportService reportService;

    @PostMapping("/{id}/report")
    public ResponseEntity<ApiResponse<Void>> reportJob(
            @PathVariable Long id,
            @Valid @RequestBody ReportRequest request,
            @AuthenticationPrincipal User user
    ) {
        reportService.reportJob(id, request.getReason(), request.getComment(), user);
        return ResponseEntity.ok(ApiResponse.success(null,"Job reported successfully"));
    }

    @Data
    public static class ReportRequest {
        @NotNull
        private ReportReason reason;

        @NotBlank
        private String comment;
    }
}