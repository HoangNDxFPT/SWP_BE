package com.example.druguseprevention.controller;

import com.example.druguseprevention.entity.CourseQuizResult;
import com.example.druguseprevention.service.CourseQuizResultService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
@SecurityRequirement(name = "api")
@SecurityRequirement(name = "bearer-key")
@RestController
@RequestMapping("/api/quiz-result")
@RequiredArgsConstructor
public class CourseQuizResultController {

    private final CourseQuizResultService service;

    //  User và Admin đều được phép tạo
    @PostMapping
    public ResponseEntity<CourseQuizResult> create(@RequestBody CourseQuizResult result) {
        return ResponseEntity.ok(service.create(result));
    }

    // Chỉ Admin xem toàn bộ kết quả
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<CourseQuizResult> getAll() {
        return service.findAll();
    }

    // Admin hoặc chính chủ user mới được xem
    @GetMapping("/{id}")
    public ResponseEntity<CourseQuizResult> getById(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(service.findById(id));
    }

    // Chỉ admin được cập nhật
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<CourseQuizResult> update(@PathVariable Long id, @RequestBody CourseQuizResult result) {
        return ResponseEntity.ok(service.update(id, result));
    }

    // Chỉ admin được xóa
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok("Deleted");
    }
}
