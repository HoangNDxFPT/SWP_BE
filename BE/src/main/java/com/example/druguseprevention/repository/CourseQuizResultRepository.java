package com.example.druguseprevention.repository;

import com.example.druguseprevention.entity.CourseQuizResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CourseQuizResultRepository extends JpaRepository<CourseQuizResult, Long> {
    List<CourseQuizResult> findByUserId(Long userId);
    @Query("SELECT COALESCE(AVG(r.score * 1.0), 0.0) FROM CourseQuizResult r")
    double getAverageScore();

}