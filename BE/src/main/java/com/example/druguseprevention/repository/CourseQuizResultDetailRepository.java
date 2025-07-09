package com.example.druguseprevention.repository;

import com.example.druguseprevention.entity.CourseQuizResultDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseQuizResultDetailRepository extends JpaRepository<CourseQuizResultDetail, Long> {
    static List<CourseQuizResultDetail> findByQuizResultId(Long quizResultId) {
        return null;
    }
}
