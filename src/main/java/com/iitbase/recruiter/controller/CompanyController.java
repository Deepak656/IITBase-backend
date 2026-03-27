package com.iitbase.recruiter.controller;

import com.iitbase.common.ApiResponse;
import com.iitbase.recruiter.dto.request.CreateCompanyRequest;
import com.iitbase.recruiter.dto.request.UpdateCompanyRequest;
import com.iitbase.recruiter.dto.response.CompanyResponse;
import com.iitbase.recruiter.service.CompanyService;
import com.iitbase.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<ApiResponse<CompanyResponse>> createCompany(
            @Valid @RequestBody CreateCompanyRequest request,
            @AuthenticationPrincipal User user) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        companyService.createCompany(request, user.getId()),
                        "Company created successfully"
                ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CompanyResponse>> getCompany(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(companyService.getCompanyById(id))
        );
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<ApiResponse<CompanyResponse>> updateCompany(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCompanyRequest request,
            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(ApiResponse.success(
                companyService.updateCompany(id, request, user.getId()),
                "Company updated successfully"
        ));
    }
}