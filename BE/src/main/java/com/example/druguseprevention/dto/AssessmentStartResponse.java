package com.example.druguseprevention.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;


@Data
public class AssessmentStartResponse {
    // Annotation này dùng để không hiện id và id tự động điền
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)

    private Long assessmentId;
    private String type;
    private String message;
    private List<QuestionDTO> questions;

    @Data
    public static class QuestionDTO {
        private Long id;
        private String questionText;
        private List<AnswerDTO> answers;
    }

    @Data
    public static class AnswerDTO {
        private Long id;
        private String text;
//        private Integer score;
    }
}