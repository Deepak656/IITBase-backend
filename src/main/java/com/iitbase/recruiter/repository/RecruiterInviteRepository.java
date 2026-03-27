package com.iitbase.recruiter.repository;

import com.iitbase.recruiter.entity.RecruiterInvite;
import com.iitbase.recruiter.enums.InviteStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecruiterInviteRepository extends JpaRepository<RecruiterInvite, Long> {

    Optional<RecruiterInvite> findByToken(String token);

    // Prevent duplicate pending invites to same email+company
    boolean existsByEmailAndCompanyIdAndStatus(
            String email, Long companyId, InviteStatus status);

    // List pending invites for a company (shown in team management UI)
    Page<RecruiterInvite> findByCompanyIdAndStatus(
            Long companyId, InviteStatus status, Pageable pageable);

    // Check if an email was already invited (any status)
    Optional<RecruiterInvite> findByEmailAndCompanyId(String email, Long companyId);
}