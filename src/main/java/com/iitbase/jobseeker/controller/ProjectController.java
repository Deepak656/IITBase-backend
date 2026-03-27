package com.iitbase.jobseeker.controller;

import com.iitbase.common.ApiResponse;
import com.iitbase.jobseeker.dto.ProjectDTO;
import com.iitbase.jobseeker.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/profile/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProjectDTO>>> getAll(Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success(projectService.getAll(auth.getName())));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProjectDTO>> add(
            @Valid @RequestBody ProjectDTO dto, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(projectService.add(auth.getName(), dto), "Project added"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectDTO>> update(
            @PathVariable Long id, @Valid @RequestBody ProjectDTO dto, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success(projectService.update(auth.getName(), id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id, Authentication auth) {
        projectService.delete(auth.getName(), id);
        return ResponseEntity.ok(ApiResponse.success(null, "Project deleted"));
    }
}