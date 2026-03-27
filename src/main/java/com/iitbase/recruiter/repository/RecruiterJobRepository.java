package com.iitbase.recruiter.repository;

import com.iitbase.recruiter.entity.RecruiterJob;
import com.iitbase.recruiter.enums.RecruiterJobStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecruiterJobRepository
        extends JpaRepository<RecruiterJob, Long>,
        JpaSpecificationExecutor<RecruiterJob> {

    long countByRecruiter_IdAndStatus(Long recruiterId, RecruiterJobStatus status);
    long countByRecruiter_Id(Long recruiterId);
    long countByStatus(RecruiterJobStatus status);
    Page<RecruiterJob> findByCompany_Id(Long companyId, Pageable pageable);
    List<RecruiterJob> findByRecruiterId(Long recruiterId);

}