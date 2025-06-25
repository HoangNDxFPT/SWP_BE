package com.example.druguseprevention.repository;

import com.example.druguseprevention.entity.Assessment;

import com.example.druguseprevention.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssessmentRepository extends JpaRepository<Assessment, Long> {
    List<Assessment> findByMember(User user);
    Optional<Assessment> findFirstByMemberOrderByCreatedAtDesc(User user);
}
