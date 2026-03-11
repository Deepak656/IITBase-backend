package com.iitbase.jobseeker.repository;

import com.iitbase.jobseeker.model.Jobseeker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface JobseekerRepository extends JpaRepository<Jobseeker, Long> {
    Optional<Jobseeker> findByUserId(String userId);
    Optional<Jobseeker> findByEmail(String email);
    boolean existsByUserId(String userId);
}
