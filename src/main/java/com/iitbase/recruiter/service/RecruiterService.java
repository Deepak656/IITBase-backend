package com.iitbase.recruiter.service;

import com.iitbase.email.event.RecruiterWelcomeEvent;
import com.iitbase.recruiter.dto.request.CreateRecruiterProfileRequest;
import com.iitbase.recruiter.dto.request.UpdateRecruiterProfileRequest;
import com.iitbase.recruiter.dto.response.RecruiterProfileResponse;
import com.iitbase.recruiter.entity.Company;
import com.iitbase.recruiter.entity.Recruiter;
import com.iitbase.recruiter.enums.TeamMemberRole;
import com.iitbase.recruiter.exception.CompanyNotFoundException;
import com.iitbase.recruiter.exception.RecruiterNotFoundException;
import com.iitbase.recruiter.exception.RecruiterProfileAlreadyExistsException;
import com.iitbase.recruiter.repository.CompanyRepository;
import com.iitbase.recruiter.repository.RecruiterRepository;
import com.iitbase.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RecruiterService {

    private final RecruiterRepository recruiterRepository;
    private final CompanyRepository companyRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final UserRepository userRepository;
    public RecruiterProfileResponse createProfile(Long userId,
                                                  CreateRecruiterProfileRequest request) {
        if (recruiterRepository.existsByUserId(userId)) {
            throw new RecruiterProfileAlreadyExistsException();
        }

        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new CompanyNotFoundException(request.getCompanyId()));

        List<Recruiter> existing = recruiterRepository.findAllByCompanyId(company.getId());
        TeamMemberRole role = existing.isEmpty() ? TeamMemberRole.ADMIN : TeamMemberRole.MEMBER;

        Recruiter recruiter = Recruiter.builder()
                .userId(userId)
                .workEmail(request.getWorkEmail())   // add this line
                .phone(request.getPhone())           // add this line if phone collected
                .company(company)
                .name(request.getName())
                .designation(request.getDesignation())
                .role(role)
                .build();

        Recruiter saved = recruiterRepository.save(recruiter);
        // Fetch email from user table (recruiter entity only stores userId)
        userRepository.findById(userId).ifPresent(user ->
                eventPublisher.publishEvent(new RecruiterWelcomeEvent(
                        this,
                        user.getEmail(),
                        saved.getName(),
                        company.getName()
                ))
        );
        log.info("Recruiter profile created userId={} companyId={} role={}",
                userId, company.getId(), role);
        return RecruiterProfileResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public RecruiterProfileResponse getMyProfile(Long userId) {
        Recruiter recruiter = recruiterRepository.findByUserId(userId)
                .orElseThrow(() -> new RecruiterNotFoundException(userId));
        return RecruiterProfileResponse.from(recruiter);
    }

    public RecruiterProfileResponse updateMyProfile(Long userId,
                                                    UpdateRecruiterProfileRequest request) {
        Recruiter recruiter = recruiterRepository.findByUserId(userId)
                .orElseThrow(() -> new RecruiterNotFoundException(userId));

        if (request.getName() != null && !request.getName().isBlank()) {
            recruiter.setName(request.getName());
        }
        if (request.getDesignation() != null) {
            recruiter.setDesignation(request.getDesignation());
        }

        log.info("Recruiter profile updated userId={}", userId);
        return RecruiterProfileResponse.from(recruiterRepository.save(recruiter));
    }

    @Transactional(readOnly = true)
    public RecruiterProfileResponse getPublicProfile(Long recruiterId) {
        Recruiter recruiter = recruiterRepository.findById(recruiterId)
                .orElseThrow(() -> new RecruiterNotFoundException(recruiterId));
        return RecruiterProfileResponse.from(recruiter);
    }
}