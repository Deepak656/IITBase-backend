package com.iitbase.application.repository;

import com.iitbase.application.entity.JobInvite;
import com.iitbase.application.enums.InviteStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JobInviteRepository extends JpaRepository<JobInvite, Long> {

    boolean existsByRecruiterIdAndJobseekerIdAndRecruiterJobId(
            Long recruiterId, Long jobseekerId, Long recruiterJobId);

    Page<JobInvite> findByJobseekerIdOrderByCreatedAtDesc(
            Long jobseekerId, Pageable pageable);

    Page<JobInvite> findByRecruiterIdOrderByCreatedAtDesc(
            Long recruiterId, Pageable pageable);

    Optional<JobInvite> findByIdAndJobseekerId(Long id, Long jobseekerId);
}