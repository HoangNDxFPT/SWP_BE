package com.example.druguseprevention.repository;

import com.example.druguseprevention.entity.UserAssessmentAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserAssessmentAnswerRepository extends JpaRepository<UserAssessmentAnswer, Long> {
    List<UserAssessmentAnswer> findByAssessmentResultId(Long assessmentResultId);

    // Lấy câu trả lời theo substance
    List<UserAssessmentAnswer> findByAssessmentResultIdAndSubstanceId(Long assessmentResultId, Long substanceId);

    // Lấy câu trả lời không liên quan substance (Q8 injection)
    List<UserAssessmentAnswer> findByAssessmentResultIdAndSubstanceIsNull(Long assessmentResultId);

    // Lấy tất cả substances được sử dụng trong một assessment result
    @Query("SELECT DISTINCT ua.substance FROM UserAssessmentAnswer ua WHERE ua.assessmentResult.id = :resultId AND ua.substance IS NOT NULL")
    List<com.example.druguseprevention.entity.Substance> findDistinctSubstancesByAssessmentResultId(@Param("resultId") Long resultId);
}
