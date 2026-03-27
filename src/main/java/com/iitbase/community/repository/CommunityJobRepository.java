package com.iitbase.community.repository;

import com.iitbase.community.entity.CommunityJob;
import com.iitbase.community.enums.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityJobRepository extends JpaRepository<CommunityJob, Long>, JpaSpecificationExecutor<CommunityJob> {

    // Use in admin panel
    List<CommunityJob> findByStatusIn(List<JobStatus> statuses);

    @Query("SELECT j FROM CommunityJob j WHERE j.status = :status ORDER BY j.createdAt ASC")
    List<CommunityJob> findByStatusOrderByCreatedAtAsc(@Param("status") JobStatus status);
    // Count jobs by user and status
    long countBySubmittedByAndStatus(Long userId, JobStatus status);

    // Count all jobs by user
    long countBySubmittedBy(Long userId);
    long countByStatus(JobStatus status);
}
