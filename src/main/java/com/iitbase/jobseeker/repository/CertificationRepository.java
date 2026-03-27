package com.iitbase.jobseeker.repository;

import com.iitbase.jobseeker.model.Certification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CertificationRepository extends JpaRepository<Certification, Long> {
    List<Certification> findByJobseekerIdOrderByDisplayOrderAscIssueYearDesc(Long jobseekerId);
    void deleteByJobseekerId(Long jobseekerId);
    boolean existsByIdAndJobseekerId(Long id, Long jobseekerId);
}