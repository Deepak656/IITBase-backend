package com.iitbase.jobseeker.repository;

import com.iitbase.jobseeker.model.JobPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface JobPreferenceRepository extends JpaRepository<JobPreference, Long> {
    Optional<JobPreference> findByJobseekerId(Long jobseekerId);
    void deleteByJobseekerId(Long jobseekerId);
}
