package com.iitbase.jobseeker.controller;

import com.iitbase.common.ApiResponse;
import com.iitbase.jobseeker.dto.JobseekerBasicInfoRequest;
import com.iitbase.jobseeker.dto.JobseekerProfileDTO;
import com.iitbase.jobseeker.service.JobseekerProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class JobseekerProfileController {

    private final JobseekerProfileService profileService;

    /**
     * GET /api/v1/profile
     * Full profile — all sections in one response.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<JobseekerProfileDTO>> getProfile(Authentication auth) {
        JobseekerProfileDTO profile = profileService.getFullProfile(auth.getName());
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    /**
     * PUT /api/v1/profile/basic
     * Update name, headline, summary, links, career break flag.
     */
    @PutMapping("/basic")
    public ResponseEntity<ApiResponse<JobseekerProfileDTO>> updateBasicInfo(
            @Valid @RequestBody JobseekerBasicInfoRequest request,
            Authentication auth) {
        JobseekerProfileDTO updated = profileService.updateBasicInfo(auth.getName(), request);
        return ResponseEntity.ok(ApiResponse.success(updated, "Profile updated"));
    }

    /**
     * POST /api/v1/profile/resume
     * Upload resume PDF — max 5MB. Replaces existing.
     */
    @PostMapping(value = "/resume", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> uploadResume(
            @RequestParam("file") MultipartFile file,
            Authentication auth) {
        String url = profileService.uploadResume(auth.getName(), file);
        return ResponseEntity.ok(ApiResponse.success(url, "Resume uploaded"));
    }

    /**
     * POST /api/v1/profile/photo
     * Upload profile photo — max 2MB, jpeg/png/webp. Replaces existing.
     */
    @PostMapping(value = "/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> uploadPhoto(
            @RequestParam("file") MultipartFile file,
            Authentication auth) {
        String url = profileService.uploadProfilePhoto(auth.getName(), file);
        return ResponseEntity.ok(ApiResponse.success(url, "Photo uploaded"));
    }
}