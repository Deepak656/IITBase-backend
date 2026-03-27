package com.iitbase.admin.recruiter;

import com.iitbase.admin.recruiter.dto.AdminRecruiterResponse;
import com.iitbase.auth.TokenService;
import com.iitbase.recruiter.entity.Recruiter;
import com.iitbase.recruiter.exception.RecruiterNotFoundException;
import com.iitbase.recruiter.repository.RecruiterRepository;
import com.iitbase.user.User;
import com.iitbase.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminRecruiterService {

    private final RecruiterRepository recruiterRepository;
    private final UserRepository      userRepository;
    private final TokenService        tokenService;

    // ── List all recruiters ───────────────────────────────────────────────────
    public Page<AdminRecruiterResponse> getAllRecruiters(int page, int size) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        return recruiterRepository.findAll(pageable)
                .map(this::toResponse);
    }

    // ── Get single recruiter ──────────────────────────────────────────────────
    public AdminRecruiterResponse getRecruiter(Long recruiterId) {
        Recruiter recruiter = recruiterRepository.findById(recruiterId)
                .orElseThrow(() -> new RecruiterNotFoundException(recruiterId));
        return toResponse(recruiter);
    }

    // ── Suspend recruiter — invalidate all their tokens ───────────────────────
    // No DB schema change needed — they simply can't log in via valid token
    // When isSuspended=true is needed persistently, add field + migration later
    @Transactional
    public AdminRecruiterResponse suspendRecruiter(Long recruiterId) {
        Recruiter recruiter = recruiterRepository.findById(recruiterId)
                .orElseThrow(() -> new RecruiterNotFoundException(recruiterId));

        User user = resolveUser(recruiter.getUserId());
        tokenService.invalidateAllUserTokens(user.getEmail());

        log.warn("Admin suspended recruiter: id={} email={}", recruiterId, user.getEmail());
        return AdminRecruiterResponse.from(recruiter, user.getEmail(), true);
    }

    // ── Unsuspend recruiter — tokens were already wiped, they just log in again
    // This is a no-op at token level — just a confirmation response
    @Transactional
    public AdminRecruiterResponse unsuspendRecruiter(Long recruiterId) {
        Recruiter recruiter = recruiterRepository.findById(recruiterId)
                .orElseThrow(() -> new RecruiterNotFoundException(recruiterId));

        User user = resolveUser(recruiter.getUserId());

        log.info("Admin unsuspended recruiter: id={} email={}", recruiterId, user.getEmail());
        return AdminRecruiterResponse.from(recruiter, user.getEmail(), false);
    }

    // ── List recruiters by company ────────────────────────────────────────────
    public Page<AdminRecruiterResponse> getRecruitersByCompany(Long companyId,
                                                               int page, int size) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        return recruiterRepository.findAllByCompanyId(companyId, pageable)
                .map(this::toResponse);
    }

    // ── Private helpers ───────────────────────────────────────────────────────
    private AdminRecruiterResponse toResponse(Recruiter r) {
        User user = resolveUser(r.getUserId());
        // No persistent suspended state yet — always false until field added
        return AdminRecruiterResponse.from(r, user.getEmail(), false);
    }

    private User resolveUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found for id: " + userId));
    }
}