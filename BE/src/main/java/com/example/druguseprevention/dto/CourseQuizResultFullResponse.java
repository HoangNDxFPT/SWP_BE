package com.example.druguseprevention.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseQuizResultFullResponse {
    private Long id;
    private int score;
    private int totalQuestions;
    private String submittedAt;
    private CourseDto course;
    private String courseStatus;
    private List<CourseQuizResultDetailDto> details;
}
