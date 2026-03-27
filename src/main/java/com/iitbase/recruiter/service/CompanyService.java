package com.iitbase.recruiter.service;

import com.iitbase.recruiter.dto.request.CreateCompanyRequest;
import com.iitbase.recruiter.dto.request.UpdateCompanyRequest;
import com.iitbase.recruiter.dto.response.CompanyResponse;
import com.iitbase.recruiter.entity.Company;
import com.iitbase.recruiter.entity.Recruiter;
import com.iitbase.recruiter.enums.CompanyStatus;
import com.iitbase.recruiter.exception.CompanyNotFoundException;
import com.iitbase.recruiter.exception.UnauthorizedActionException;
import com.iitbase.recruiter.repository.CompanyRepository;
import com.iitbase.recruiter.repository.RecruiterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final RecruiterRepository recruiterRepository;

    public CompanyResponse createCompany(CreateCompanyRequest request, Long userId) {
        if (companyRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException(
                    "A company named '" + request.getName() + "' already exists on IITBase. " +
                            "Search for it and request to join instead.");
        }

        // Extract email domain from work email if provided
        String emailDomain = extractDomain(request.getWorkEmail());

        Company company = Company.builder()
                .name(request.getName())
                .website(request.getWebsite())
                .emailDomain(emailDomain)
                .industry(request.getIndustry())
                .size(request.getSize())
                .description(request.getDescription())
                .isVerified(false)
                .createdByUserId(userId)
                .status(CompanyStatus.PENDING)   // IITBase admin will verify
                .build();

        Company saved = companyRepository.save(company);
        log.info("Company created id={} name={} domain={} by userId={}",
                saved.getId(), saved.getName(), emailDomain, userId);

        return CompanyResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public CompanyResponse getCompanyById(Long id) {
        return CompanyResponse.from(
                companyRepository.findById(id)
                        .orElseThrow(() -> new CompanyNotFoundException(id)));
    }

    public CompanyResponse updateCompany(Long companyId,
                                         UpdateCompanyRequest request,
                                         Long userId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException(companyId));

        Recruiter recruiter = recruiterRepository.findByUserId(userId)
                .orElseThrow(() -> new UnauthorizedActionException(
                        "No recruiter profile found for this user"));

        if (!recruiter.getIsAdmin()
                || !recruiter.getCompany().getId().equals(companyId)) {
            throw new UnauthorizedActionException(
                    "Only the company admin can update company details");
        }

        if (request.getWebsite() != null)     company.setWebsite(request.getWebsite());
        if (request.getIndustry() != null)    company.setIndustry(request.getIndustry());
        if (request.getSize() != null)        company.setSize(request.getSize());
        if (request.getDescription() != null) company.setDescription(request.getDescription());
        if (request.getLogoUrl() != null)     company.setLogoUrl(request.getLogoUrl());

        log.info("Company id={} updated by userId={}", companyId, userId);
        return CompanyResponse.from(companyRepository.save(company));
    }

    // ── IITBase admin operations ──────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<CompanyResponse> getPendingCompanies(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return companyRepository.findByStatus(CompanyStatus.PENDING, pageable)
                .map(CompanyResponse::from);
    }

    /**
     * IITBase admin verifies a company.
     * Sets status=VERIFIED and isVerified=true (legacy field).
     */
    public CompanyResponse verifyCompany(Long companyId, Long adminUserId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException(companyId));

        company.setStatus(CompanyStatus.VERIFIED);
        company.setIsVerified(true);
        company.setReviewedBy(adminUserId);

        log.info("Company verified id={} by adminUserId={}", companyId, adminUserId);
        return CompanyResponse.from(companyRepository.save(company));
    }

    /**
     * IITBase admin rejects a company.
     */
    public CompanyResponse rejectCompany(Long companyId, Long adminUserId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException(companyId));

        company.setStatus(CompanyStatus.REJECTED);
        company.setReviewedBy(adminUserId);

        log.info("Company rejected id={} by adminUserId={}", companyId, adminUserId);
        return CompanyResponse.from(companyRepository.save(company));
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    /**
     * Extracts domain from an email address.
     * "rahul@google.com" → "google.com"
     * Returns null if email is null or malformed.
     */
    public static String extractDomain(String email) {
        if (email == null || email.isBlank()) return null;
        int atIdx = email.indexOf('@');
        if (atIdx < 0 || atIdx == email.length() - 1) return null;
        String domain = email.substring(atIdx + 1).toLowerCase().trim();
        // Reject generic free email domains — not useful as company signals
        if (domain.equals("gmail.com")
                || domain.equals("yahoo.com")
                || domain.equals("hotmail.com")
                || domain.equals("outlook.com")) {
            return null;
        }
        return domain;
    }
}