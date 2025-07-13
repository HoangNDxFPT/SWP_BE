package com.example.druguseprevention.repository;

import com.example.druguseprevention.entity.CourseQuizResultDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseQuizResultDetailRepository extends JpaRepository<CourseQuizResultDetail, Long> {
    List<CourseQuizResultDetail> findAllByQuizResult_User_Id(Long userId);
    List<CourseQuizResultDetail> findAllByQuizResult_Id(Long resultId);

}