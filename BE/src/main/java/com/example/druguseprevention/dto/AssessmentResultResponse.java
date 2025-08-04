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
    private int score;
    private RiskLevel riskLevel;
    private String recommendation;
    private LocalDateTime submittedAt;
    private List<CourseDTO> recommendedCourses;
    private List<AnswerDetail> answers;


    @Data
    public static class AnswerDetail {
        private Long questionId;
        private String questionText;
        private Long answerId;
        private String answerText;
        private Integer score;
        // Thêm thông tin substance
        private Long substanceId;
        private String substanceName;
        private String substanceDescription;
    }

    @Data
    public static class CourseDTO {
        private Long id;
        private String name;
        private String description;
        private String targetAgeGroup;
    }
}
