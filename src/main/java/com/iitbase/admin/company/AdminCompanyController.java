package com.iitbase.admin.company;

import com.iitbase.admin.company.dto.AdminCompanyResponse;
import com.iitbase.admin.company.dto.AdminEditCompanyRequest;
import com.iitbase.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/companies")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCompanyController {

    private final AdminCompanyService adminCompanyService;

    // List — GET /api/admin/companies?page=0&size=20&search=acme&verified=false
    @GetMapping
    public ResponseEntity<ApiResponse<Page<AdminCompanyResponse>>> getAllCompanies(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false)    String search,
            @RequestParam(required = false)    Boolean verified) {
        return ResponseEntity.ok(ApiResponse.success(
                adminCompanyService.getAllCompanies(page, size, search, verified)));
    }

    // Detail — GET /api/admin/companies/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminCompanyResponse>> getCompany(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                adminCompanyService.getCompany(id)));
    }

    // Verify — POST /api/admin/companies/{id}/verify
    @PostMapping("/{id}/verify")
    public ResponseEntity<ApiResponse<AdminCompanyResponse>> verifyCompany(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                adminCompanyService.verifyCompany(id),
                "Company verified"));
    }

    // Unverify — POST /api/admin/companies/{id}/unverify
    @PostMapping("/{id}/unverify")
    public ResponseEntity<ApiResponse<AdminCompanyResponse>> unverifyCompany(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                adminCompanyService.unverifyCompany(id),
                "Company unverified"));
    }

    // Edit — PATCH /api/admin/companies/{id}
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminCompanyResponse>> editCompany(
            @PathVariable Long id,
            @RequestBody AdminEditCompanyRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                adminCompanyService.editCompany(id, request),
                "Company updated"));
    }
}