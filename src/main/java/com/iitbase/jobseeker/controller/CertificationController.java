package com.iitbase.jobseeker.controller;

import com.iitbase.common.ApiResponse;
import com.iitbase.jobseeker.dto.CertificationDTO;
import com.iitbase.jobseeker.service.CertificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/profile/certifications")
@RequiredArgsConstructor
public class CertificationController {

    private final CertificationService certificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CertificationDTO>>> getAll(Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success(certificationService.getAll(auth.getName())));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CertificationDTO>> add(
            @Valid @RequestBody CertificationDTO dto, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(certificationService.add(auth.getName(), dto), "Certification added"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CertificationDTO>> update(
            @PathVariable Long id, @Valid @RequestBody CertificationDTO dto, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success(certificationService.update(auth.getName(), id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id, Authentication auth) {
        certificationService.delete(auth.getName(), id);
        return ResponseEntity.ok(ApiResponse.success(null, "Certification deleted"));
    }
}