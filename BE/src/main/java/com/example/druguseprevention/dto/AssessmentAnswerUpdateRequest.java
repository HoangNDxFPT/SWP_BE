package com.example.druguseprevention.dto;

import lombok.Data;

@Data
public class AssessmentAnswerUpdateRequest {
    private Long id;
    private String answerText;
    private Integer score;
}
