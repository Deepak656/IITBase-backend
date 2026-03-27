package com.iitbase.admin.company;

import com.iitbase.admin.company.dto.AdminCompanyResponse;
import com.iitbase.admin.company.dto.AdminEditCompanyRequest;
import com.iitbase.email.event.RecruiterCompanyVerifiedEvent;
import com.iitbase.recruiter.entity.Company;
import com.iitbase.recruiter.enums.TeamMemberRole;
import com.iitbase.recruiter.exception.CompanyNotFoundException;
import com.iitbase.recruiter.repository.CompanyRepository;
import com.iitbase.recruiter.repository.RecruiterRepository;
import com.iitbase.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AdminCompanyService {

    private final CompanyRepository   companyRepository;
    private final RecruiterRepository recruiterRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final UserRepository userRepository;
    // ── List all companies ────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public Page<AdminCompanyResponse> getAllCompanies(int page, int size,
                                                      String search,
                                                      Boolean verified) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Company> companies;

        if (search != null && !search.isBlank()) {
            companies = companyRepository
                    .findByNameContainingIgnoreCase(search.trim(), pageable);
        } else if (verified != null) {
            companies = companyRepository
                    .findByIsVerified(verified, pageable);
        } else {
            companies = companyRepository.findAll(pageable);
        }

        return companies.map(c -> AdminCompanyResponse.from(
                c, recruiterRepository.findAllByCompanyId(c.getId()).size()
        ));
    }

    // ── Get single company ────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public AdminCompanyResponse getCompany(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new CompanyNotFoundException(id));
        long count = recruiterRepository.findAllByCompanyId(id).size();
        return AdminCompanyResponse.from(company, count);
    }

    // ── Verify company ────────────────────────────────────────────────────────
    public AdminCompanyResponse verifyCompany(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new CompanyNotFoundException(id));

        company.setIsVerified(true);
        Company saved = companyRepository.save(company);

        log.info("Admin verified company: id={} name={}", id, company.getName());

        // Notify all admin recruiters of this company — they're the ones "going live"
        recruiterRepository.findAllByCompanyId(id)
                .stream()
                .filter(r -> r.getRole() == TeamMemberRole.ADMIN)
                .forEach(adminRecruiter ->
                        userRepository.findById(adminRecruiter.getUserId()).ifPresent(user ->
                                eventPublisher.publishEvent(new RecruiterCompanyVerifiedEvent(
                                        this,
                                        user.getEmail(),
                                        adminRecruiter.getName(),
                                        saved.getName()
                                ))
                        )
                );

        long count = recruiterRepository.findAllByCompanyId(id).size();
        return AdminCompanyResponse.from(saved, count);
    }

    // ── Unverify company ──────────────────────────────────────────────────────
    public AdminCompanyResponse unverifyCompany(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new CompanyNotFoundException(id));
        company.setIsVerified(false);
        Company saved = companyRepository.save(company);
        log.warn("Admin unverified company: id={} name={}", id, company.getName());
        long count = recruiterRepository.findAllByCompanyId(id).size();
        return AdminCompanyResponse.from(saved, count);
    }

    // ── Edit company (admin override) ─────────────────────────────────────────
    public AdminCompanyResponse editCompany(Long id, AdminEditCompanyRequest request) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new CompanyNotFoundException(id));

        if (request.getName()        != null) company.setName(request.getName());
        if (request.getWebsite()     != null) company.setWebsite(request.getWebsite());
        if (request.getIndustry()    != null) company.setIndustry(request.getIndustry());
        if (request.getSize()        != null) company.setSize(request.getSize());
        if (request.getDescription() != null) company.setDescription(request.getDescription());
        if (request.getLogoUrl()     != null) company.setLogoUrl(request.getLogoUrl());

        Company saved = companyRepository.save(company);
        log.info("Admin edited company: id={}", id);
        long count = recruiterRepository.findAllByCompanyId(id).size();
        return AdminCompanyResponse.from(saved, count);
    }
}