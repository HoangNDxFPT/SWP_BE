package com.example.druguseprevention.service;

import com.example.druguseprevention.dto.CourseQuizDto;

import java.util.List;

public interface CourseQuizService {
    List<CourseQuizDto> getQuizByCourseId(Long courseId);
    CourseQuizDto createQuiz(CourseQuizDto dto);
    void deleteQuiz(Long id);
    CourseQuizDto updateQuiz(Long id, CourseQuizDto dto);
    List<Long> getCompletedCourseIdsByUserId(Long userId);
}