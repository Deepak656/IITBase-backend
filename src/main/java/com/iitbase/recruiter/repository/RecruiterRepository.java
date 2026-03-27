package com.iitbase.recruiter.repository;

import com.iitbase.recruiter.entity.Recruiter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecruiterRepository extends JpaRepository<Recruiter, Long> {
    Optional<Recruiter> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
    List<Recruiter> findAllByCompanyId(Long companyId);
    Page<Recruiter> findAllByCompanyId(Long companyId, Pageable pageable);
}