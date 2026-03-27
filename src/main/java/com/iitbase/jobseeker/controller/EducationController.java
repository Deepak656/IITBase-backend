package com.iitbase.jobseeker.controller;

import com.iitbase.common.ApiResponse;
import com.iitbase.jobseeker.dto.EducationDTO;
import com.iitbase.jobseeker.service.EducationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/profile/education")
@RequiredArgsConstructor
public class EducationController {

    private final EducationService educationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<EducationDTO>>> getAll(Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success(educationService.getAll(auth.getName())));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EducationDTO>> add(
            @Valid @RequestBody EducationDTO dto, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(educationService.add(auth.getName(), dto), "Education added"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EducationDTO>> update(
            @PathVariable Long id, @Valid @RequestBody EducationDTO dto, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success(educationService.update(auth.getName(), id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id, Authentication auth) {
        educationService.delete(auth.getName(), id);
        return ResponseEntity.ok(ApiResponse.success(null, "Education deleted"));
    }
}