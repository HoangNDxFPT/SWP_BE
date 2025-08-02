package com.example.druguseprevention.repository;

import com.example.druguseprevention.entity.AssessmentResult;
import com.example.druguseprevention.enums.RiskLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AssessmentResultRepository extends JpaRepository<AssessmentResult, Long> {
    // Tìm kết quả theo ID bài đánh giá
    Optional<AssessmentResult> findByAssessmentId(Long assessmentId);

    // Tìm tất cả kết quả theo user (qua Assessment)
    List<AssessmentResult> findByAssessmentMemberId(Long userId);

    // Đếm số lượng theo từng mức độ rủi ro
    long countByRiskLevel(RiskLevel riskLevel);

    // Tính điểm trung bình của tất cả bài đánh giá
    @Query("SELECT COALESCE(AVG(ar.score * 1.0), 0.0) FROM AssessmentResult ar")
    double getAverageAssessmentScore();

    // Đếm số assessment result trong khoảng thời gian
    @Query("SELECT COUNT(ar) FROM AssessmentResult ar WHERE ar.dateTaken >= :startDate AND ar.dateTaken <= :endDate")
    long countByDateTakenBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
