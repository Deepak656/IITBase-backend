package com.iitbase.recruiter.repository;

import com.iitbase.recruiter.entity.TeamJoinRequest;
import com.iitbase.recruiter.enums.JoinRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamJoinRequestRepository extends JpaRepository<TeamJoinRequest, Long> {

    // Check if user already has a pending/approved request for this company
    boolean existsByUserIdAndCompanyIdAndStatus(
            Long userId, Long companyId, JoinRequestStatus status);

    Optional<TeamJoinRequest> findByUserIdAndCompanyId(Long userId, Long companyId);

    // Admin of a company fetches pending requests
    Page<TeamJoinRequest> findByCompanyIdAndStatus(
            Long companyId, JoinRequestStatus status, Pageable pageable);

    // User checks their own request status
    Optional<TeamJoinRequest> findByUserIdAndCompanyIdAndStatus(
            Long userId, Long companyId, JoinRequestStatus status);
}