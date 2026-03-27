package com.iitbase.recruiter.controller;

import com.iitbase.common.ApiResponse;
import com.iitbase.recruiter.dto.request.AcceptInviteRequest;
import com.iitbase.recruiter.dto.request.JoinCompanyRequest;
import com.iitbase.recruiter.dto.response.CompanySearchResult;
import com.iitbase.recruiter.dto.response.JoinRequestResponse;
import com.iitbase.recruiter.dto.response.RecruiterProfileResponse;
import com.iitbase.recruiter.service.CompanyService;
import com.iitbase.recruiter.service.TeamService;
import com.iitbase.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/companies")
@RequiredArgsConstructor
public class CompanySearchController {

    private final TeamService teamService;
    private final CompanyService companyService;

    /**
     * GET /api/v1/companies/search?q=google&domain=google.com
     *
     * Used in onboarding step 1 when user types their company name.
     * Pass the domain extracted from their work email to get domainMatch flags.
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<ApiResponse<List<CompanySearchResult>>> search(
            @RequestParam String q,
            @RequestParam(required = false) String domain,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(ApiResponse.success(
                teamService.searchCompanies(q, domain, page, size)));
    }

    /**
     * POST /api/v1/companies/{id}/join-request
     *
     * Path B: user requests to join an existing company (no domain match).
     */
    @PostMapping("/{companyId}/join-request")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<ApiResponse<JoinRequestResponse>> requestToJoin(
            @PathVariable Long companyId,
            @Valid @RequestBody JoinCompanyRequest request,
            @AuthenticationPrincipal User user) {

        request.setCompanyId(companyId); // ensure path param wins
        return ResponseEntity.ok(ApiResponse.success(
                teamService.requestToJoin(user.getId(), request),
                "Join request sent to company admin"));
    }

    /**
     * POST /api/v1/companies/invite/accept
     *
     * Invited user accepts their invite after signing up.
     */
    @PostMapping("/invite/accept")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<ApiResponse<RecruiterProfileResponse>> acceptInvite(
            @Valid @RequestBody AcceptInviteRequest request,
            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(ApiResponse.success(
                teamService.acceptInvite(user.getId(), request),
                "Welcome to the team!"));
    }

    // ── IITBase admin endpoints ───────────────────────────────────────────────

    /**
     * GET /api/v1/companies/pending — IITBase admin sees unverified companies
     */
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> getPendingCompanies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(
                ApiResponse.success(companyService.getPendingCompanies(page, size)));
    }

    /**
     * PATCH /api/v1/companies/{id}/verify — IITBase admin verifies a company
     */
    @PatchMapping("/{id}/verify")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> verifyCompany(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(ApiResponse.success(
                companyService.verifyCompany(id, user.getId()),
                "Company verified"));
    }

    /**
     * PATCH /api/v1/companies/{id}/reject — IITBase admin rejects a company
     */
    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> rejectCompany(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(ApiResponse.success(
                companyService.rejectCompany(id, user.getId()),
                "Company rejected"));
    }
}