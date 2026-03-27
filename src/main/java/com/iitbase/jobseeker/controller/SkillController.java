package com.iitbase.jobseeker.controller;

import com.iitbase.common.ApiResponse;
import com.iitbase.jobseeker.dto.SkillDTO;
import com.iitbase.jobseeker.service.SkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/profile/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SkillDTO>>> getAll(Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success(skillService.getAll(auth.getName())));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SkillDTO>> add(
            @Valid @RequestBody SkillDTO dto, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(skillService.add(auth.getName(), dto), "Skill added"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SkillDTO>> update(
            @PathVariable Long id, @Valid @RequestBody SkillDTO dto, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success(skillService.update(auth.getName(), id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id, Authentication auth) {
        skillService.delete(auth.getName(), id);
        return ResponseEntity.ok(ApiResponse.success(null, "Skill deleted"));
    }
}