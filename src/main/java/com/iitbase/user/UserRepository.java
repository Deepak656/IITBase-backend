package com.iitbase.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByRole(UserRole role);

    long countByRole(UserRole role);

    Page<User> findAll(Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.college = :college")
    List<User> findByCollege(@Param("college") String college);

    @Query("SELECT u FROM User u WHERE u.graduationYear = :year")
    List<User> findByGraduationYear(@Param("year") Integer year);

    @Query("SELECT u FROM User u WHERE u.role = :role AND u.college = :college")
    List<User> findByRoleAndCollege(@Param("role") UserRole role, @Param("college") String college);
}