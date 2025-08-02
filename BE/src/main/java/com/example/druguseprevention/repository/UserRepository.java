package com.example.druguseprevention.repository;

import com.example.druguseprevention.entity.User;
import com.example.druguseprevention.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserName(String userName); // ✅ Đã đúng

    List<User> findByDeletedFalse();

    // Thêm dòng này để tìm tất cả user có role = MEMBER
    List<User> findByRoleAndDeletedFalse(Role role);
    boolean existsByEmail(String email);

    Optional<Object> findByIdAndDeletedFalse(Long consultantId);
    List<User> findByRole(Role role);

    // New methods for dashboard analytics
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt >= :startDate AND u.createdAt <= :endDate AND u.deleted = false")
    long countByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(u) FROM User u WHERE u.lastLoginAt >= :startDate AND u.deleted = false")
    long countActiveUsersSince(@Param("startDate") LocalDateTime startDate);
}
