package com.iitbase.jobseeker.controller;

import com.iitbase.common.ApiResponse;
import com.iitbase.jobseeker.dto.JobPreferenceDTO;
import com.iitbase.jobseeker.service.JobPreferenceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profile/job-preferences")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "https://iitbase.com"})
public class JobPreferenceController {
    
    private final JobPreferenceService jobPreferenceService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<JobPreferenceDTO>> getJobPreference(Authentication authentication) {
        try {
            String userId = authentication.getName(); // JWT subject (user ID)
            log.info("GET /api/v1/profile/job-preferences - userId: {}", userId);
            
            JobPreferenceDTO preference = jobPreferenceService.getJobPreference(userId);
            
            if (preference == null) {
                return ResponseEntity
                        .ok(ApiResponse.success(null, "No job preferences found"));
            }
            
            return ResponseEntity
                    .ok(ApiResponse.success(preference,"Job preferences retrieved successfully"));
                    
        } catch (Exception e) {
            log.error("Error fetching job preferences", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch job preferences"));
        }
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<JobPreferenceDTO>> createOrUpdateJobPreference(
            @Valid @RequestBody JobPreferenceDTO dto,
            Authentication authentication) {
        try {
            String userId = authentication.getName();
            log.info("POST /api/v1/profile/job-preferences - userId: {}", userId);
            
            JobPreferenceDTO savedPreference = jobPreferenceService.saveOrUpdateJobPreference(userId, dto);
            
            return ResponseEntity
                    .ok(ApiResponse.success(savedPreference,"Job preferences saved successfully"));
                    
        } catch (Exception e) {
            log.error("Error saving job preferences", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to save job preferences"));
        }
    }
    
    @PutMapping
    public ResponseEntity<ApiResponse<JobPreferenceDTO>> updateJobPreference(
            @Valid @RequestBody JobPreferenceDTO dto,
            Authentication authentication) {
        try {
            String userId = authentication.getName();
            log.info("PUT /api/v1/profile/job-preferences - userId: {}", userId);
            
            JobPreferenceDTO updatedPreference = jobPreferenceService.saveOrUpdateJobPreference(userId, dto);
            
            return ResponseEntity
                    .ok(ApiResponse.success(updatedPreference, "Job preferences updated successfully"));
                    
        } catch (Exception e) {
            log.error("Error updating job preferences", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update job preferences"));
        }
    }
    
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteJobPreference(Authentication authentication) {
        try {
            String userId = authentication.getName();
            log.info("DELETE /api/v1/profile/job-preferences - userId: {}", userId);
            
            jobPreferenceService.deleteJobPreference(userId);
            
            return ResponseEntity
                    .ok(ApiResponse.success(null,"Job preferences deleted successfully"));
                    
        } catch (Exception e) {
            log.error("Error deleting job preferences", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to delete job preferences"));
        }
    }
}
