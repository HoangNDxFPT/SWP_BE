package com.example.druguseprevention.controller;

import com.example.druguseprevention.entity.Lesson;
import com.example.druguseprevention.service.LessonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lessons")
public class LessonController {

    private final LessonService lessonService;

    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @PostMapping
    public ResponseEntity<?> createLesson(@RequestBody Lesson lesson) {
        return ResponseEntity.ok(lessonService.createLesson(lesson));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getLesson(@PathVariable Long id) {
        return lessonService.getLesson(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Lesson>> getLessonsByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(lessonService.getLessonsByCourseId(courseId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateLesson(@PathVariable Long id, @RequestBody Lesson lesson) {
        return ResponseEntity.ok(lessonService.updateLesson(id, lesson));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLesson(@PathVariable Long id) {
        lessonService.deleteLesson(id);
        return ResponseEntity.ok("Deleted successfully");
    }
}
