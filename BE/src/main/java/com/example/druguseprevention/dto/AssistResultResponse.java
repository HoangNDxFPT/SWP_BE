package com.example.druguseprevention.dto;

import com.example.druguseprevention.enums.RiskLevel;
import lombok.Data;
import java.util.List;

@Data
public class AssistResultResponse {
    private Long assessmentResultId;
    private Long assessmentId;
    private String assessmentType = "ASSIST";
    private List<SubstanceResult> substanceResults;
    private RiskLevel overallRiskLevel; // Mức độ rủi ro tổng thể (cao nhất trong các chất)
    private String recommendation;
    private java.time.LocalDateTime submittedAt;
    private List<CourseDTO> recommendedCourses;

    @Data
    public static class SubstanceResult {
        private Long substanceId;
        private String substanceName;
        private String substanceDescription;
        private int score;
        private RiskLevel riskLevel;
        private String criteria;
        private List<QuestionAnswer> answers;
    }

    @Data
    public static class QuestionAnswer {
        private String questionText;
        private String answerText;
        private int score;
    }

    @Data
    public static class CourseDTO {
        private Long id;
        private String name;
        private String description;
        private String targetAgeGroup;
    }
}
