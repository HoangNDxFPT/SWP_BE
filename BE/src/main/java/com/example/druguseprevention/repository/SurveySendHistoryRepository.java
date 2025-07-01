package com.example.druguseprevention.repository;

import com.example.druguseprevention.entity.SurveySendHistory;
import com.example.druguseprevention.enums.SurveyType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SurveySendHistoryRepository extends JpaRepository<SurveySendHistory, Long> {
    List<SurveySendHistory> findByProgramIdAndTemplateType(Long programId, SurveyType templateType);
}
