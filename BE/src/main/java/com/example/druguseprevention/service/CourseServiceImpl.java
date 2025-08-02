package com.example.druguseprevention.service;

import com.example.druguseprevention.entity.Course;
import com.example.druguseprevention.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor // Tự động tạo constructor cho final field
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    // Lấy danh sách khóa học (lọc theo tên nếu có), chỉ lấy các khóa học chưa bị xóa
    @Override
    public List<Course> getCourses(String name) {
        if (name != null && !name.isEmpty()) {
            return courseRepository.findByNameContainingIgnoreCaseAndIsDeletedFalse(name);
        }
        return courseRepository.findByIsDeletedFalse();
    }

    // Lấy chi tiết 1 khóa học theo ID (bỏ qua nếu đã bị đánh dấu xóa)
    @Override
    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .filter(course -> !course.isDeleted())
                .orElse(null);
    }

    // Tạo mới 1 khóa học (đánh dấu là chưa bị xóa)
    @Override
    public Course create(Course course) {
        course.setIsDeleted(false);
        return courseRepository.save(course);
    }

    // Cập nhật thông tin khóa học theo ID (chỉ cập nhật nếu chưa bị xóa)
    @Override
    public Course update(Long id, Course course) {
        return courseRepository.findById(id)
                .filter(c -> !c.isDeleted())
                .map(existing -> {
                    existing.setName(course.getName());
                    existing.setDescription(course.getDescription());
                    existing.setStartDate(course.getStartDate());
                    existing.setEndDate(course.getEndDate());
                    existing.setTargetAgeGroup(course.getTargetAgeGroup());
                    existing.setUrl(course.getUrl());
                    existing.setDurationInMinutes(course.getDurationInMinutes());
                    return courseRepository.save(existing);
                })
                .orElse(null);
    }

    // Xóa mềm khóa học (set isDeleted = true thay vì xóa khỏi DB)
    @Override
    public void delete(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khóa học"));
        course.setIsDeleted(true);
        courseRepository.save(course);
    }

    // Lấy danh sách tất cả khóa học chưa bị xóa
    @Override
    public List<Course> getCourseList() {
        return courseRepository.findByIsDeletedFalse();
    }

    // Tìm course theo ID (có thể trả về cả course đã bị xóa)
    public Optional<Course> findById(Long id) {
        return courseRepository.findById(id);
    }
}
