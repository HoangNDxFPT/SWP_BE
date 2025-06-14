package com.example.druguseprevention.service;

import com.example.druguseprevention.entity.Course;
import com.example.druguseprevention.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    public List<Course> getCourses(String name) {
        if (name != null && !name.isEmpty()) {
            return courseRepository.findByNameContainingIgnoreCase(name);
        }
        return courseRepository.findAll();
    }

    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
    }
    public Course create(Course course) {
        return courseRepository.save(course);
    }

    public Course update(Long id, Course course) {
        return courseRepository.findById(id).map(existing -> {
            existing.setName(course.getName());
            existing.setDescription(course.getDescription());
            existing.setStartDate(course.getStartDate());
            existing.setEndDate(course.getEndDate());
            existing.setTargetAgeGroup(course.getTargetAgeGroup());
            existing.setType(course.getType());
            existing.setUrl(course.getUrl());
            return courseRepository.save(existing);
        }).orElseThrow(() -> new RuntimeException("Course not found"));
    }

    public void delete(Long id) {
        courseRepository.deleteById(id);
    }

}
