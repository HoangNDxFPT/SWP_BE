package com.example.druguseprevention.repository;

import com.example.druguseprevention.entity.RecommendationCourse;

import com.example.druguseprevention.entity.RecommendationCourseId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecommendationCourseRepository extends JpaRepository<RecommendationCourse, RecommendationCourseId> {
    List<RecommendationCourse> findByIdAssessmentResultId(Long assessmentResultId);
}
