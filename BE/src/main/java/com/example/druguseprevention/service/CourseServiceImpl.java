package com.example.druguseprevention.service;

import com.example.druguseprevention.entity.Course;
import com.example.druguseprevention.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl {

    private final CourseRepository courseRepository;

    public List<Course> getCourses(String name) {
        if (name != null && !name.isEmpty()) {
            return courseRepository.findByNameContainingIgnoreCaseAndIsDeletedFalse(name);
        }
        return courseRepository.findByIsDeletedFalse();
    }

    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .filter(course -> !course.isDeleted()) // đảm bảo không lấy khóa học đã bị xóa
                .orElse(null);
    }

    public Course create(Course course) {
        course.setIsDeleted(false); // mặc định chưa xóa
        return courseRepository.save(course);
    }

    public Course update(Long id, Course course) {
        return courseRepository.findById(id).filter(c -> !c.isDeleted()).map(existing -> {
            existing.setName(course.getName());
            existing.setDescription(course.getDescription());
            existing.setStartDate(course.getStartDate());
            existing.setEndDate(course.getEndDate());
            existing.setTargetAgeGroup(course.getTargetAgeGroup());
            existing.setType(course.getType());
            existing.setUrl(course.getUrl());
            return courseRepository.save(existing);
        }).orElse(null);
    }

    public void delete(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khóa học"));
        course.setIsDeleted(true);
        courseRepository.save(course); // chỉ đánh dấu, không xóa thật
    }
    public List<Course> getCourseList() {
        return courseRepository.findByIsDeletedFalse(); // Chỉ lấy khóa học chưa bị xóa
    }

}

