package com.example.druguseprevention.service;

import com.example.druguseprevention.entity.Course;
import com.example.druguseprevention.entity.Enrollment;
import com.example.druguseprevention.entity.EnrollmentId;
import com.example.druguseprevention.entity.User;
import com.example.druguseprevention.repository.CourseRepository;
import com.example.druguseprevention.repository.EnrollmentRepository;
import com.example.druguseprevention.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    @Override
    public Enrollment enrollUserToCourse(Long courseId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        EnrollmentId id = new EnrollmentId(user.getId(), courseId);
        if (enrollmentRepository.existsById(id)) {
            throw new RuntimeException("User already enrolled in this course");
        }

        Enrollment enrollment = Enrollment.builder()
                .id(id)
                .member(user)
                .course(course)
                .enrollDate(LocalDateTime.now())
                .status(Enrollment.Status.InProgress)
                .build();

        return enrollmentRepository.save(enrollment);
    }

    @Override
    public Enrollment enrollUserInCourse(User user, Course course) {
        EnrollmentId id = new EnrollmentId(user.getId(), course.getId());

        return enrollmentRepository.findById(id).orElseGet(() -> {
            Enrollment enrollment = Enrollment.builder()
                    .id(id)
                    .member(user)
                    .course(course)
                    .enrollDate(LocalDateTime.now())
                    .status(Enrollment.Status.InProgress)
                    .build();
            return enrollmentRepository.save(enrollment);
        });
    }

    @Override
    public List<Enrollment> getEnrollmentsByUser(User user) {
        return enrollmentRepository.findByMember(user);
    }

    @Override
    public List<Course> getCoursesByUser(User user) {
        return enrollmentRepository.findCoursesByUser(user);
    }

    @Override
    public List<User> getUsersByCourse(Course course) {
        return enrollmentRepository.findUsersByCourse(course);
    }
}
