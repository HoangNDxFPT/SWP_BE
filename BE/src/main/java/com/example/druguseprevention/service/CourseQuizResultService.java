package com.example.druguseprevention.service;

import com.example.druguseprevention.entity.CourseQuizResult;

import java.util.List;

public interface CourseQuizResultService {
    CourseQuizResult create(CourseQuizResult result);
    List<CourseQuizResult> findAll();
    CourseQuizResult findById(Long id);
    CourseQuizResult update(Long id, CourseQuizResult updatedResult);
    void delete(Long id);
}
