package com.example.druguseprevention.service;

import com.example.druguseprevention.entity.Course;
import com.example.druguseprevention.entity.Enrollment;
import com.example.druguseprevention.entity.EnrollmentId;
import com.example.druguseprevention.entity.User;
import com.example.druguseprevention.repository.CourseRepository;
import com.example.druguseprevention.repository.EnrollmentRepository;
import com.example.druguseprevention.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public abstract class EnrollmentService {
    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    public Enrollment enrollUserToCourse(Long courseId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        EnrollmentId id = new EnrollmentId(user.getId(), courseId);
        if (enrollmentRepository.existsById(id)) {
            throw new RuntimeException("User already enrolled in this course");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setId(id);
        enrollment.setMember(user);
        enrollment.setCourse(course);
        enrollment.setEnrollDate(LocalDateTime.now());
        enrollment.setStatus(Enrollment.Status.InProgress);

        return enrollmentRepository.save(enrollment);
    }

    public abstract Enrollment enrollUserInCourse(User user, Course course);

    public abstract List<Enrollment> getEnrollmentsByUser(User user);
}
