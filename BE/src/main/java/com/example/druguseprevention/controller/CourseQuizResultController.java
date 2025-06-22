package com.example.druguseprevention.controller;

import com.example.druguseprevention.entity.CourseQuizResult;
import com.example.druguseprevention.service.CourseQuizResultService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@SecurityRequirement(name = "api")
@SecurityRequirement(name = "bearer-key")
@RestController
@RequestMapping("/api/quiz-result")
@RequiredArgsConstructor
public class CourseQuizResultController {

    private final CourseQuizResultService service;

    @PostMapping
    public ResponseEntity<CourseQuizResult> create(@RequestBody CourseQuizResult result) {
        return ResponseEntity.ok(service.create(result));
    }

    @GetMapping
    public List<CourseQuizResult> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseQuizResult> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseQuizResult> update(@PathVariable Long id, @RequestBody CourseQuizResult result) {
        return ResponseEntity.ok(service.update(id, result));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok("Deleted");
    }
}
