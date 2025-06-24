package com.example.druguseprevention.repository;

import com.example.druguseprevention.entity.User;
import com.example.druguseprevention.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserName(String userName); // ✅ Đã đúng

    List<User> findByDeletedFalse();

    // ✅ Thêm dòng này để tìm tất cả user có role = MEMBER
    List<User> findByRoleAndDeletedFalse(Role role);

}


