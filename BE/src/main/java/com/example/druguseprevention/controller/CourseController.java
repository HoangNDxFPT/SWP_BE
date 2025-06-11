package com.example.druguseprevention.controller;

import com.example.druguseprevention.entity.Course;
import com.example.druguseprevention.service.CourseService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    @SecurityRequirement(name = "api")
    @GetMapping
    public ResponseEntity<List<Course>> getCourses(@RequestParam(required = false) String name) {
        return ResponseEntity.ok(courseService.getCourses(name));
    }
    @SecurityRequirement(name = "api")
    @GetMapping("/{id}")
    public ResponseEntity<Course> getById(@PathVariable Long id) {
        Course course = courseService.getCourseById(id);
        return course != null ? ResponseEntity.ok(course) : ResponseEntity.notFound().build();
    }
    @SecurityRequirement(name = "api")
    @PostMapping
    public ResponseEntity<Course> create(@RequestBody Course course) {
        return ResponseEntity.ok(courseService.create(course));
    }
    @SecurityRequirement(name = "api")
    @PutMapping("/{id}")
    public ResponseEntity<Course> update(@PathVariable Long id, @RequestBody Course course) {
        Course updated = courseService.update(id, course);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }
    @SecurityRequirement(name = "api")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        courseService.delete(id);
        return ResponseEntity.ok("Đã xóa khóa học");
    }
}