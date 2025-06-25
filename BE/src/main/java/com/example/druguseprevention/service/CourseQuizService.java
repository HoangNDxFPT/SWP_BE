package com.example.druguseprevention.service;

import com.example.druguseprevention.dto.CourseQuizDto;

import java.util.List;

public interface CourseQuizService {
    List<CourseQuizDto> getQuizByCourseId(Long courseId);
    CourseQuizDto createQuiz(CourseQuizDto dto);
    void deleteQuiz(Long id);

    // ✅ Trả về danh sách ID khóa học mà user đã hoàn thành (điểm >= 60%)
    List<Long> getCompletedCourseIdsByUserId(Long userId);
}
