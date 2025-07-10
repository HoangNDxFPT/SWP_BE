package com.example.druguseprevention.repository;

import com.example.druguseprevention.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findByIsDeletedFalse();
    List<Course> findByNameContainingIgnoreCaseAndIsDeletedFalse(String name);


    List<Course> findByNameContainingIgnoreCase(String name);
    List<Course> findByTargetAgeGroup(Course.TargetAgeGroup group);


}

