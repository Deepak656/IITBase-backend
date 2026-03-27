package com.iitbase.jobseeker.repository;

import com.iitbase.jobseeker.model.Education;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EducationRepository extends JpaRepository<Education, Long> {
    List<Education> findByJobseekerIdOrderByDisplayOrderAscEndYearDesc(Long jobseekerId);
    void deleteByJobseekerId(Long jobseekerId);
    boolean existsByIdAndJobseekerId(Long id, Long jobseekerId);
}