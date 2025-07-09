package com.example.druguseprevention.service;

import com.example.druguseprevention.dto.CourseQuizResultDetailDto;
import com.example.druguseprevention.entity.CourseQuizResult;

import java.util.List;
public interface CourseQuizResultService {
    CourseQuizResult create(CourseQuizResult result);
    List<CourseQuizResult> findAll();
    CourseQuizResult findById(Long id);
    CourseQuizResult update(Long id, CourseQuizResult updatedResult);
    void delete(Long id);
    boolean isOwner(Long resultId, Long userId);
    public List<CourseQuizResultDetailDto> getResultDetails(Long quizResultId);
    List<CourseQuizResult> findByUserId(Long userId);

}
