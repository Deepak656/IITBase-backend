package com.iitbase.jobseeker.repository;

import com.iitbase.jobseeker.model.Jobseeker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JobseekerRepository extends JpaRepository<Jobseeker, Long> {
    Optional<Jobseeker> findByUserId(Long userId);   // Long, not String
    Optional<Jobseeker> findByEmail(String email);
    boolean existsByUserId(Long userId);
    Page<Jobseeker> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<Jobseeker> findByIsVerified(Boolean isVerified, Pageable pageable);

    Page<Jobseeker> findByFullNameContainingIgnoreCase(String name, Pageable pageable);
}