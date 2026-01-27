package com.iitbase.removal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRemovalRepository extends JpaRepository<JobRemovalRequest, Long> {
}