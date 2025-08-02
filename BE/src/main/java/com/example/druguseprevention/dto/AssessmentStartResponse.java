package com.example.druguseprevention.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;


@Data
public class AssessmentStartResponse {
    // Annotation này dùng để không hiện id và id tự động điền
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)

    private String type;
    private String message;
    private List<QuestionDTO> questions;
    private List<SubstanceDTO> substances; // Thêm danh sách substances đã chọn

    @Data
    public static class QuestionDTO {
        private Long id;
        private String questionText;
        private List<AnswerDTO> answers;
        private Long substanceId; // Thêm substanceId để biết câu hỏi này cho chất nào
    }

    @Data
    public static class AnswerDTO {
        private Long id;
        private String text;
//        private Integer score;
    }

    @Data
    public static class SubstanceDTO {
        private Long id;
        private String name;
        private String description;
    }
}