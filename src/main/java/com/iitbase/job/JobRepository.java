package com.iitbase.job;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {

    // Use in admin panel
    List<Job> findByStatusIn(List<JobStatus> statuses);

    @Query("SELECT j FROM Job j WHERE j.status = :status ORDER BY j.createdAt ASC")
    List<Job> findByStatusOrderByCreatedAtAsc(@Param("status") JobStatus status);
    // Count jobs by user and status
    long countBySubmittedByAndStatus(Long userId, JobStatus status);

    // Count all jobs by user
    long countBySubmittedBy(Long userId);
}