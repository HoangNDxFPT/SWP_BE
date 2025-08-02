package com.example.druguseprevention.dto;

import com.example.druguseprevention.enums.AssessmentType;
import com.example.druguseprevention.enums.RiskLevel;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AssessmentResultResponse {
    private Long assessmentResultId;
    private Long assessmentId;
    private AssessmentType assessmentType;
    private int totalScore;
    private RiskLevel overallRiskLevel;
    private String recommendation;
    private LocalDateTime submittedAt;
    private List<CourseDTO> recommendedCourses;
    private List<AnswerDetail> answers;

    // Thêm kết quả theo từng substance cho ASSIST
    private List<SubstanceResult> substanceResults;

    @Data
    public static class AnswerDetail {
        private Long questionId;
        private String questionText;
        private Long answerId;
        private String answerText;
        private Integer score;
        private SubstanceDTO substance;
    }

    @Data
    public static class SubstanceResult {
        private SubstanceDTO substance;
        private int score;
        private RiskLevel riskLevel;
        private String riskCriteria;
        private List<AnswerDetail> answers;
    }

    @Data
    public static class CourseDTO {
        private Long id;
        private String name;
        private String description;
        private String targetAgeGroup;
    }

    @Data
    public static class SubstanceDTO {
        private Long id;
        private String name;
        private String description;
    }
}
