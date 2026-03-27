package com.iitbase.application.repository;

import com.iitbase.application.entity.Application;
import com.iitbase.application.enums.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicationRepository
        extends JpaRepository<Application, Long>,
        JpaSpecificationExecutor<Application> {

    // Idempotency check — one application per jobseeker per job
    boolean existsByRecruiterJobIdAndJobseekerId(
            Long recruiterJobId, Long jobseekerId);

    Optional<Application> findByIdAndJobseekerId(
            Long id, Long jobseekerId);

    Optional<Application> findByIdAndRecruiterId(
            Long id, Long recruiterId);

    Page<Application> findByJobseekerId(
            Long jobseekerId, Pageable pageable);

    Page<Application> findByRecruiterJobIdAndStatusNot(
            Long recruiterJobId,
            ApplicationStatus status,   // excludes WITHDRAWN
            Pageable pageable);

    long countByRecruiterJobId(Long recruiterJobId);

    long countByRecruiterJobIdAndStatus(
            Long recruiterJobId, ApplicationStatus status);
}