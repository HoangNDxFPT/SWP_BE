package com.example.druguseprevention.repository;

import com.example.druguseprevention.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthenticationRepository extends JpaRepository<User, Long> {
    User findUserByUserName (String userName);
    User findUserByEmail (String email);
}
