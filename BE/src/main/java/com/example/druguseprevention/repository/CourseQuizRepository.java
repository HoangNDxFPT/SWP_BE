package com.example.druguseprevention.repository;

import com.example.druguseprevention.entity.CourseQuiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseQuizRepository extends JpaRepository<CourseQuiz, Long> {
    List<CourseQuiz> findByCourseId(Long courseId);
}
