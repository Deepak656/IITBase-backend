package com.iitbase.recruiter.repository;

import com.iitbase.recruiter.entity.Company;
import com.iitbase.recruiter.enums.CompanyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    Optional<Company> findByName(String name);
    boolean existsByName(String name);

    // Domain-based lookup — core of Path A trust flow
    Optional<Company> findByEmailDomain(String emailDomain);
    boolean existsByEmailDomain(String emailDomain);
    List<Company> findAllByEmailDomain(String emailDomain);

    // Search by partial name — used in onboarding company search
    Page<Company> findByNameContainingIgnoreCaseAndStatus(
            String name, CompanyStatus status, Pageable pageable);

    Page<Company> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // Admin queries
    Page<Company> findByStatus(CompanyStatus status, Pageable pageable);
    long countByStatus(CompanyStatus status);

    // Legacy
    long countByIsVerified(boolean isVerified);
    Page<Company> findByIsVerified(boolean isVerified, Pageable pageable);
}