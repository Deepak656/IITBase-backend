package com.iitbase.jobseeker.repository;

import com.iitbase.jobseeker.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByJobseekerIdOrderByDisplayOrderAscStartYearDesc(Long jobseekerId);
    void deleteByJobseekerId(Long jobseekerId);
    boolean existsByIdAndJobseekerId(Long id, Long jobseekerId);
}