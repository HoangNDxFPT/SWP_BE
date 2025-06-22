package com.example.druguseprevention.repository;

import com.example.druguseprevention.entity.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, Long> {
    // Không cần viết hàm save(), findById() vì JpaRepository đã có sẵn
}
