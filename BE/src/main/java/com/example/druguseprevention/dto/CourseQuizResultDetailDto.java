package com.example.druguseprevention.dto;

import lombok.Data;

import java.util.List;

@Data
public class CourseQuizResultDetailDto {
    private String question;

    private List<String> options;

    private String correctAnswer;
    private String studentAnswer;
    private boolean isCorrect; // Lombok tự tạo getIsCorrect() và setCorrect() — KHÔNG cần viết tay!
}
