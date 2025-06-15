package com.example.druguseprevention.controller;


import com.example.druguseprevention.entity.LessonProgress;
import com.example.druguseprevention.service.LessonProgressService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/progress")
public class LessonProgressController {

    private final LessonProgressService service;

    public LessonProgressController(LessonProgressService service) {
        this.service = service;
    }

    //  Đánh dấu đã học xong bài
    @PostMapping("/complete")
    public ResponseEntity<LessonProgress> completeLesson(@RequestParam Long userId, @RequestParam Long lessonId) {
        return ResponseEntity.ok(service.completeLesson(userId, lessonId));
    }

    // Gửi kết quả quiz
    @PostMapping("/quiz")
    public ResponseEntity<LessonProgress> submitQuiz(
            @RequestParam Long userId,
            @RequestParam Long lessonId,
            @RequestParam boolean passed
    ) {
        return ResponseEntity.ok(service.submitQuiz(userId, lessonId, passed));
    }

    //  Xem toàn bộ tiến trình học của người dùng
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<LessonProgress>> getUserProgress(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getUserProgress(userId));
    }
}
