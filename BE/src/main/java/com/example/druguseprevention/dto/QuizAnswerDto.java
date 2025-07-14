package com.example.druguseprevention.dto;

import lombok.Data;

import java.util.List;

@Data
public class QuizAnswerDto {
    private String question;
    private List<String> options;
    private String correctAnswer;
    private String studentAnswer;
}
