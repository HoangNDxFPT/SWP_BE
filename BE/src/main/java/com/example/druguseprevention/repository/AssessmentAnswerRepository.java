package com.example.druguseprevention.repository;

import com.example.druguseprevention.entity.AssessmentAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssessmentAnswerRepository extends JpaRepository<AssessmentAnswer, Long> {
    List<AssessmentAnswer> findByQuestionId(Long questionId);
    List<AssessmentAnswer> findByIsDeletedFalse();
    Optional<AssessmentAnswer> findByIdAndIsDeletedFalse(Long id);
}
