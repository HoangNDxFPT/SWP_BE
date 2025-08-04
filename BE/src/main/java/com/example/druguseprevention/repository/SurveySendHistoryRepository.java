package com.example.druguseprevention.repository;

import com.example.druguseprevention.entity.SurveySendHistory;
import com.example.druguseprevention.enums.SurveyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface SurveySendHistoryRepository extends JpaRepository<SurveySendHistory, Long> {
    List<SurveySendHistory> findByProgramIdAndTemplateType(Long programId, SurveyType templateType);
    List<SurveySendHistory> findByUserId (Long id);
    List<SurveySendHistory> findByProgramId (Long id);

    @Query("SELECT COUNT(s) FROM SurveySendHistory s " +
            "WHERE s.program.id = :programId " +
            "AND s.templateType = :surveyType " +
            "AND s.status = com.example.druguseprevention.enums.SurveySendStatus.SENT")
    long countSentSurveysByProgramAndType(
            @Param("programId") Long programId,
            @Param("surveyType") SurveyType surveyType
    );
}
