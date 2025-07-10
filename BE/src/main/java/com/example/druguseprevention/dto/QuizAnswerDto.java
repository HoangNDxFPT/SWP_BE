package com.example.druguseprevention.dto;

import lombok.Data;

@Data
public class QuizAnswerDto {
    private String question;
    private String options;
    private String correctAnswer;
    private String studentAnswer;
}
