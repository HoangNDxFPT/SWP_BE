package com.example.druguseprevention.controller;

import com.example.druguseprevention.entity.Course;
import com.example.druguseprevention.entity.Enrollment;
import com.example.druguseprevention.entity.User;
import com.example.druguseprevention.repository.CourseRepository;
import com.example.druguseprevention.repository.UserRepository;
import com.example.druguseprevention.service.EnrollmentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "api")
@SecurityRequirement(name = "bearer-key")
@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public EnrollmentController(EnrollmentService enrollmentService, UserRepository userRepository, CourseRepository courseRepository) {
        this.enrollmentService = enrollmentService;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    @PostMapping("/enroll")
    public ResponseEntity<?> enroll(@RequestParam Long userId, @RequestParam Long courseId) {
        User user = userRepository.findById(userId).orElse(null);
        Course course = courseRepository.findById(courseId).orElse(null);
        if (user == null || course == null) {
            return ResponseEntity.badRequest().body("User hoặc Course không tồn tại.");
        }
        Enrollment enrolled = enrollmentService.enrollUserInCourse(user, course);
        return ResponseEntity.ok(enrolled);
    }
    //Lấy danh sách khóa học mà một user đã đăng ký
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getCoursesByUser(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body("User không tồn tại.");
        }
        List<Course> courses = enrollmentService.getCoursesByUser(user);
        return ResponseEntity.ok(courses);
    }
    //Lấy danh sách user đã đăng ký một khóa học
    @GetMapping("/course/{courseId}")
    public ResponseEntity<?> getUsersByCourse(@PathVariable Long courseId) {
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null) {
            return ResponseEntity.badRequest().body("Course không tồn tại.");
        }
        List<User> users = enrollmentService.getUsersByCourse(course);
        return ResponseEntity.ok(users);
    }

}
