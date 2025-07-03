package com.example.druguseprevention.repository;

import com.example.druguseprevention.entity.SurveyTemplate;
import com.example.druguseprevention.enums.SurveyType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SurveyTemplateRepository extends JpaRepository<SurveyTemplate, Long> {
    List<SurveyTemplate> findByIsDeletedFalse();
    Optional<SurveyTemplate> findByIdAndIsDeletedFalse(Long id);
    Optional<SurveyTemplate> findByProgramIdAndTypeAndIsDeletedFalse(Long programId, SurveyType type);
}
