package com.example.druguseprevention.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.example.druguseprevention.entity.CourseQuizResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface CourseQuizResultRepository extends JpaRepository<CourseQuizResult, Long> {
    List<CourseQuizResult> findByUserId(Long userId);
    @Query("SELECT COALESCE(AVG(r.score * 1.0), 0.0) FROM CourseQuizResult r")
    double getAverageScore();

    Optional<CourseQuizResult> findTopByUser_IdOrderBySubmittedAtDesc(Long userId);
}