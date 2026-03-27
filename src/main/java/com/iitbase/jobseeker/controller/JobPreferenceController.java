package com.iitbase.jobseeker.controller;

import com.iitbase.common.ApiResponse;
import com.iitbase.jobseeker.dto.JobPreferenceDTO;
import com.iitbase.jobseeker.service.JobPreferenceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/profile/job-preferences")
@RequiredArgsConstructor
public class JobPreferenceController {

    private final JobPreferenceService jobPreferenceService;

    @GetMapping
    public ResponseEntity<ApiResponse<JobPreferenceDTO>> get(Authentication auth) {
        JobPreferenceDTO preference = jobPreferenceService.getJobPreference(auth.getName());
        return ResponseEntity.ok(ApiResponse.success(preference));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<JobPreferenceDTO>> createOrUpdate(
            @Valid @RequestBody JobPreferenceDTO dto, Authentication auth) {
        JobPreferenceDTO saved = jobPreferenceService.saveOrUpdateJobPreference(auth.getName(), dto);
        return ResponseEntity.ok(ApiResponse.success(saved, "Job preferences saved"));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<JobPreferenceDTO>> update(
            @Valid @RequestBody JobPreferenceDTO dto, Authentication auth) {
        JobPreferenceDTO updated = jobPreferenceService.saveOrUpdateJobPreference(auth.getName(), dto);
        return ResponseEntity.ok(ApiResponse.success(updated, "Job preferences updated"));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> delete(Authentication auth) {
        jobPreferenceService.deleteJobPreference(auth.getName());
        return ResponseEntity.ok(ApiResponse.success(null, "Job preferences deleted"));
    }
}