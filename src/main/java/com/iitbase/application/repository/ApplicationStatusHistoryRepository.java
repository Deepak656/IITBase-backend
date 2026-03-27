package com.iitbase.application.repository;

import com.iitbase.application.entity.ApplicationStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationStatusHistoryRepository
        extends JpaRepository<ApplicationStatusHistory, Long> {

    // Full audit trail for one application, oldest first
    List<ApplicationStatusHistory> findByApplication_IdOrderByCreatedAtAsc(
            Long applicationId);
}