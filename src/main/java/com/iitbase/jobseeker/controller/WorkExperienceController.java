package com.iitbase.jobseeker.controller;

import com.iitbase.common.ApiResponse;
import com.iitbase.jobseeker.dto.WorkExperienceDTO;
import com.iitbase.jobseeker.service.WorkExperienceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/profile/experience")
@RequiredArgsConstructor
public class WorkExperienceController {

    private final WorkExperienceService workExperienceService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<WorkExperienceDTO>>> getAll(Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success(workExperienceService.getAll(auth.getName())));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<WorkExperienceDTO>> add(
            @Valid @RequestBody WorkExperienceDTO dto, Authentication auth) {
        WorkExperienceDTO saved = workExperienceService.add(auth.getName(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(saved, "Experience added"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<WorkExperienceDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody WorkExperienceDTO dto,
            Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success(workExperienceService.update(auth.getName(), id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id, Authentication auth) {
        workExperienceService.delete(auth.getName(), id);
        return ResponseEntity.ok(ApiResponse.success(null, "Experience deleted"));
    }
}