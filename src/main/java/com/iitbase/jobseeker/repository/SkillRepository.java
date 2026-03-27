package com.iitbase.jobseeker.repository;

import com.iitbase.jobseeker.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {
    List<Skill> findByJobseekerIdOrderByDisplayOrderAsc(Long jobseekerId);
    void deleteByJobseekerId(Long jobseekerId);
    boolean existsByIdAndJobseekerId(Long id, Long jobseekerId);
}