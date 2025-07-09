package com.example.druguseprevention.dto;

import lombok.Data;

@Data
public class CourseQuizResultDetailDto {
    private String question;
    private String options;
    private String correctAnswer;
    private String studentAnswer;
    private boolean isCorrect;

    public void setIsCorrect(boolean correct) {
    }
}
