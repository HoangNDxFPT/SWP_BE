package com.example.druguseprevention.service;

import com.example.druguseprevention.dto.CourseQuizResultDetailDto;
import com.example.druguseprevention.dto.CourseQuizResultDto;
import com.example.druguseprevention.dto.QuizSubmitRequest;
import com.example.druguseprevention.entity.CourseQuizResult;
import com.example.druguseprevention.entity.User;

import java.util.List;

public interface CourseQuizResultService {
    CourseQuizResult create(CourseQuizResult result);
    List<CourseQuizResult> findAll();
    CourseQuizResult findById(Long id);
    CourseQuizResult update(Long id, CourseQuizResult updatedResult);
    void delete(Long id);
    boolean isOwner(Long resultId, Long userId);
    List<CourseQuizResult> findByUserId(Long userId);
    List<CourseQuizResultDetailDto> getMyResultDetails(Long userId);
    void submitQuiz(QuizSubmitRequest request, User user);
    List<CourseQuizResultDetailDto> getResultDetailsByResultId(Long resultId);

    List<CourseQuizResultDto> getResultDtosByUserId(Long id);
}