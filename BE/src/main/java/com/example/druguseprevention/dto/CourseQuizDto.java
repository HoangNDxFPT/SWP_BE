package com.example.druguseprevention.dto;

import lombok.Data;

@Data
public class CourseQuizDto {
    private Long id;
    private Long courseId;
    private String question;
    private String answer;  // JSON string
    private Integer correct;
}