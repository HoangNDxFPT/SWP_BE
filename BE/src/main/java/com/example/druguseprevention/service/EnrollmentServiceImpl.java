package com.example.druguseprevention.service;
import com.example.druguseprevention.entity.Course;
import com.example.druguseprevention.entity.Enrollment;
import com.example.druguseprevention.entity.User;
import com.example.druguseprevention.repository.CourseRepository;
import com.example.druguseprevention.repository.EnrollmentRepository;
import com.example.druguseprevention.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public EnrollmentServiceImpl(EnrollmentRepository enrollmentRepository,
                                 UserRepository userRepository,
                                 CourseRepository courseRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public Enrollment enrollUserInCourse(User user, Course course) {
        return enrollmentRepository.findByUserAndCourse(user, course)
                .orElseGet(() -> enrollmentRepository.save(new Enrollment(null, user, course, false)));
    }

    @Override
    public List<Enrollment> getEnrollmentsByUser(User user) {
        return enrollmentRepository.findByUser(user);
    }
}