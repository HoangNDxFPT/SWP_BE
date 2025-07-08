package com.example.druguseprevention.repository;

import com.example.druguseprevention.entity.CourseQuizResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseQuizResultRepository extends JpaRepository<CourseQuizResult, Long> {
    List<CourseQuizResult> findByUserId(Long userId);

}