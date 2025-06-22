package com.example.druguseprevention.service;

import com.example.druguseprevention.entity.Course;
import com.example.druguseprevention.entity.Enrollment;
import com.example.druguseprevention.entity.EnrollmentId;
import com.example.druguseprevention.entity.User;
import com.example.druguseprevention.repository.CourseRepository;
import com.example.druguseprevention.repository.EnrollmentRepository;
import com.example.druguseprevention.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl extends EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    @Override
    public Enrollment enrollUserInCourse(User user, Course course) {
        EnrollmentId enrollmentId = new EnrollmentId(user.getId(), course.getId());

        return enrollmentRepository.findById(enrollmentId)
                .orElseGet(() -> {
                    Enrollment enrollment = Enrollment.builder()
                            .id(enrollmentId)
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
}
