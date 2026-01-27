package com.iitbase.removal;

import com.iitbase.common.ApiResponse;
import com.iitbase.user.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobRemovalController {

    private final JobRemovalService removalService;

    @PostMapping("/{id}/removal-request")
    public ResponseEntity<ApiResponse<Void>> requestRemoval(
            @PathVariable Long id,
            @Valid @RequestBody RemovalRequest request,
            @AuthenticationPrincipal User user
    ) {
        removalService.requestRemoval(id, request.getRequesterEmail(), request.getReason(), user);
        return ResponseEntity.ok(ApiResponse.success(null,"Removal request submitted"));
    }

    @Data
    public static class RemovalRequest {
        @NotBlank
        @Email
        private String requesterEmail;

        @NotBlank
        private String reason;
    }
}