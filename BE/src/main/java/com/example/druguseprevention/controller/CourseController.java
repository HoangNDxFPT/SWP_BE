package com.example.druguseprevention.controller;

import com.example.druguseprevention.entity.Course;
import com.example.druguseprevention.service.CourseServiceImpl;
import com.example.druguseprevention.service.CourseQuizService;
import com.example.druguseprevention.service.CourseService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@SecurityRequirement(name = "api")
@SecurityRequirement(name = "bearer-key")
@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseServiceImpl courseService;
    private final CourseQuizService courseQuizService; // ✅ thêm
//    private final CourseService courseService;


    @GetMapping
    public ResponseEntity<List<Course>> getCourses(@RequestParam(required = false) String name) {
        return ResponseEntity.ok(courseService.getCourses(name));
    }


    @GetMapping("/{id}")
    public ResponseEntity<Course> getById(@PathVariable Long id) {
        Course course = courseService.getCourseById(id);
        return course != null ? ResponseEntity.ok(course) : ResponseEntity.notFound().build();
    }


    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Course> create(@RequestBody Course course) {
        return ResponseEntity.ok(courseService.create(course));
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Course> update(@PathVariable Long id, @RequestBody Course course) {
        Course updated = courseService.update(id, course);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        courseService.delete(id);
        return ResponseEntity.ok("Đã xóa khóa học");
    }


    @GetMapping("/list")
    public List<Course> getCourseList() {
        return courseService.getCourseList();
    }

}
