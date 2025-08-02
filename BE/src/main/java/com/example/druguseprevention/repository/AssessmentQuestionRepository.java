package com.example.druguseprevention.repository;

import com.example.druguseprevention.entity.AssessmentQuestion;
import com.example.druguseprevention.enums.AssessmentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssessmentQuestionRepository extends JpaRepository<AssessmentQuestion, Long> {
    List<AssessmentQuestion> findByAssessmentTypeOrderByQuestionOrder(AssessmentType type);
    List<AssessmentQuestion> findByIsDeletedFalse();
    Optional<AssessmentQuestion> findByIdAndIsDeletedFalse(Long id);
    List<AssessmentQuestion> findByAssessmentTypeAndIsDeletedFalseOrderByQuestionOrder(AssessmentType type);

    // Thêm các method để tìm kiếm theo substance
    List<AssessmentQuestion> findByAssessmentTypeAndSubstanceIdAndIsDeletedFalseOrderByQuestionOrder(AssessmentType type, Long substanceId);
    List<AssessmentQuestion> findByAssessmentTypeAndSubstanceIsNullAndIsDeletedFalseOrderByQuestionOrder(AssessmentType type);
}
