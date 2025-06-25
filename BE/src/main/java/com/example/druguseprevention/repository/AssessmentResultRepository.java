package com.example.druguseprevention.repository;

import com.example.druguseprevention.entity.AssessmentResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssessmentResultRepository extends JpaRepository<AssessmentResult, Long> {
    // Tìm kết quả theo ID bài đánh giá
    Optional<AssessmentResult> findByAssessmentId(Long assessmentId);

    // Tìm tất cả kết quả theo user (qua Assessment)
    List<AssessmentResult> findByAssessmentMemberId(Long userId);
}
