package com.example.druguseprevention.dto;

import lombok.Data;
import java.util.List;

@Data
public class QuizSubmitRequest {
    private Long courseId;
    private double score;
    private List<QuizAnswerDto> answers;
}
