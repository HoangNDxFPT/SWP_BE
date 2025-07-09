package com.example.druguseprevention.service;

import com.example.druguseprevention.dto.CourseDto;
import com.example.druguseprevention.dto.EnrollmentDto;
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
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Override
    public List<EnrollmentDto> getEnrollmentDtosByUser(User user) {
        return enrollmentRepository.findByMember(user).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EnrollmentDto> getEnrollmentDtosByCourse(Course course) {
        return enrollmentRepository.findByCourse(course).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Enrollment updateEnrollment(User user, Course oldCourse, Course newCourse) {
        EnrollmentId oldId = new EnrollmentId(user.getId(), oldCourse.getId());
        enrollmentRepository.deleteById(oldId);

        EnrollmentId newId = new EnrollmentId(user.getId(), newCourse.getId());
        Enrollment enrollment = Enrollment.builder()
                .id(newId)
                .member(user)
                .course(newCourse)
                .enrollDate(LocalDateTime.now())
                .status(Enrollment.Status.InProgress)
                .build();

        return enrollmentRepository.save(enrollment);
    }

    @Override
    public boolean unenrollUserFromCourse(User user, Course course) {
        EnrollmentId id = new EnrollmentId(user.getId(), course.getId());
        if (enrollmentRepository.existsById(id)) {
            enrollmentRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // ✨ Mapping từ Entity sang DTO
    private EnrollmentDto mapToDto(Enrollment enrollment) {
        EnrollmentDto dto = new EnrollmentDto();

        User user = enrollment.getMember();
        Course course = enrollment.getCourse();

        dto.setId(enrollment.getId());
        dto.setUserId(user != null ? user.getId() : null);
        dto.setUserName(user != null ? user.getFullName() : null);
        dto.setCourseId(course != null ? course.getId() : null);
        dto.setCourseName(course != null ? course.getName() : null);
        dto.setStatus(enrollment.getStatus() != null ? enrollment.getStatus().name() : null);
        dto.setEnrolledAt(enrollment.getEnrollDate());

        return dto;
    }

    public List<CourseDto> getCoursesOfCurrentUser(User currentUser) {
        List<Course> courses = enrollmentRepository.findCoursesByUser(currentUser);

        return courses.stream()
                .map(course -> CourseDto.builder()
                        .id(course.getId())
                        .name(course.getName())
                        .description(course.getDescription())
                        .startDate(course.getStartDate())
                        .endDate(course.getEndDate())
                        .durationInMinutes(course.getDurationInMinutes())
                        .url(course.getUrl())
                        .build()
                )
                .collect(Collectors.toList());
    }

    public boolean cancelEnrollment(Long userId, Long courseId) {
        User user = userRepository.findById(userId).orElse(null);
        Course course = courseRepository.findById(courseId).orElse(null);

        if (user == null || course == null) {
            return false;
        }

        Optional<Enrollment> enrollmentOpt = enrollmentRepository.findByMemberAndCourse(user, course);
        if (enrollmentOpt.isPresent()) {
            Enrollment enrollment = enrollmentOpt.get();
            enrollment.setStatus(Enrollment.Status.Cancelled);
            enrollmentRepository.save(enrollment);
            return true;
        }

        return false;
    }

    @Override
    public Enrollment reEnrollUserToCourse(User user, Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        EnrollmentId id = new EnrollmentId(user.getId(), courseId);
        Optional<Enrollment> existing = enrollmentRepository.findById(id);

        if (existing.isPresent()) {
            Enrollment enrollment = existing.get();
            if (enrollment.getStatus() == Enrollment.Status.Cancelled) {
                enrollment.setStatus(Enrollment.Status.InProgress);
                enrollment.setEnrollDate(LocalDateTime.now());
                return enrollmentRepository.saveAndFlush(enrollment); // Force update
            } else {
                throw new RuntimeException("Bạn đã đăng ký rồi.");
            }
        }

        Enrollment newEnrollment = Enrollment.builder()
                .id(id)
                .member(user)
                .course(course)
                .enrollDate(LocalDateTime.now())
                .status(Enrollment.Status.InProgress)
                .build();

        return enrollmentRepository.save(newEnrollment);
    }

    @Override
    public List<EnrollmentDto> getAllEnrollmentDtos() {
        List<Enrollment> enrollments = enrollmentRepository.findAll();

        return enrollments.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }




}

