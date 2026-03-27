package com.iitbase.admin.staff;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StaffInviteRepository extends JpaRepository<StaffInvite, Long> {

    Optional<StaffInvite> findByToken(String token);

    boolean existsByEmailAndStatus(String email, StaffInviteStatus status);

    Page<StaffInvite> findByStatus(StaffInviteStatus status, Pageable pageable);

    Page<StaffInvite> findAllByOrderByCreatedAtDesc(Pageable pageable);
}