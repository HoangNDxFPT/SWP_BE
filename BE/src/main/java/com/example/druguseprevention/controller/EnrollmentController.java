package com.example.druguseprevention.controller;

import com.example.druguseprevention.dto.CourseDto;
import com.example.druguseprevention.dto.EnrollmentDto;
import com.example.druguseprevention.entity.Course;
import com.example.druguseprevention.entity.User;
import com.example.druguseprevention.repository.CourseRepository;
import com.example.druguseprevention.repository.UserRepository;
import com.example.druguseprevention.service.EnrollmentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
        return ResponseEntity.ok(enrollmentService.enrollUserInCourse(user, course));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getEnrollmentsByUser(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body("User không tồn tại.");
        }
        List<EnrollmentDto> enrollments = enrollmentService.getEnrollmentDtosByUser(user);
        return ResponseEntity.ok(enrollments);
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getEnrollmentsByCourse(@PathVariable Long courseId) {
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null) {
            return ResponseEntity.badRequest().body("Course không tồn tại.");
        }
        List<EnrollmentDto> enrollments = enrollmentService.getEnrollmentDtosByCourse(course);
        return ResponseEntity.ok(enrollments);
    }
    @GetMapping("/my-courses")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<?> getMyCourses(@AuthenticationPrincipal User currentUser) {
        List<CourseDto> courses = enrollmentService.getCoursesOfCurrentUser(currentUser);
        return ResponseEntity.ok(courses);
    }
}
//
//    //  PUT: Cập nhật trạng thái ghi danh hoặc chuyển khóa học
//    @PutMapping("/update")
//    public ResponseEntity<?> updateEnrollment(
//            @RequestParam Long userId,
//            @RequestParam Long oldCourseId,
//            @RequestParam Long newCourseId) {
//        User user = userRepository.findById(userId).orElse(null);
//        Course oldCourse = courseRepository.findById(oldCourseId).orElse(null);
//        Course newCourse = courseRepository.findById(newCourseId).orElse(null);
//
//        if (user == null || oldCourse == null || newCourse == null) {
//            return ResponseEntity.badRequest().body("User hoặc Course không tồn tại.");
//        }
//
//        return ResponseEntity.ok(enrollmentService.updateEnrollment(user, oldCourse, newCourse));
//    }
//
//    // DELETE: Hủy ghi danh
//    @DeleteMapping("/unenroll")
//    public ResponseEntity<?> unenroll(
//            @RequestParam Long userId,
//            @RequestParam Long courseId) {
//        User user = userRepository.findById(userId).orElse(null);
//        Course course = courseRepository.findById(courseId).orElse(null);
//
//        if (user == null || course == null) {
//            return ResponseEntity.badRequest().body("User hoặc Course không tồn tại.");
//        }
//
//        boolean success = enrollmentService.unenrollUserFromCourse(user, course);
//        if (success) {
//            return ResponseEntity.ok("Hủy ghi danh thành công.");
//        } else {
//            return ResponseEntity.badRequest().body("Ghi danh không tồn tại.");
//        }
//    }
//}