package com.example.druguseprevention.repository;
import com.example.druguseprevention.entity.SurveyResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SurveyResultRepository extends JpaRepository<SurveyResult, Long> {
    List<SurveyResult> findByUserId(Long userId);
}