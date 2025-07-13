package com.example.druguseprevention.dto;

import lombok.Data;

@Data
public class CourseQuizResultDto {

    private Long id;
    private int score;
    private int totalQuestions;
    private Long courseId;
    private String courseName;
    private String submittedAt;
}
