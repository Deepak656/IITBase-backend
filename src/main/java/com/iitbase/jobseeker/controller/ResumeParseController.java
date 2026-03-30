package com.iitbase.jobseeker.controller;

import com.iitbase.common.ApiResponse;
import com.iitbase.jobseeker.dto.ResumeParseResponseDTO;
import com.iitbase.jobseeker.service.ResumeParseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * POST /api/v1/profile/resume/parse
 *
 * Parses the user's already-uploaded resume and returns structured profile data.
 * Does NOT save anything — frontend pre-fills the form, user confirms.
 *
 * Requires resume to be uploaded first via POST /api/v1/profile/resume
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/profile/resume")
@RequiredArgsConstructor
public class ResumeParseController {

    private final ResumeParseService resumeParseService;

    @PostMapping("/parse")
    public ResponseEntity<ApiResponse<ResumeParseResponseDTO>> parse(Authentication auth) {
        log.info("Resume parse requested by: {}", auth.getName());
        ResumeParseResponseDTO parsed = resumeParseService.parseUploadedResume(auth.getName());
        return ResponseEntity.ok(ApiResponse.success(parsed, "Resume parsed successfully"));
    }
}