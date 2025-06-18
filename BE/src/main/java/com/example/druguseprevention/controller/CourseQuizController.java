package com.example.druguseprevention.controller;

import com.example.druguseprevention.dto.CourseQuizDto;
import com.example.druguseprevention.service.CourseQuizService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "api")
@SecurityRequirement(name = "bearer-key")
@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
public class CourseQuizController {

    private final CourseQuizService quizService;

    @GetMapping("/course/{courseId}")
    public List<CourseQuizDto> getQuizzesByCourse(@PathVariable Long courseId) {
        return quizService.getQuizByCourseId(courseId);
    }

    @PostMapping
    public CourseQuizDto createQuiz(@RequestBody CourseQuizDto dto) {
        return quizService.createQuiz(dto);
    }

    @DeleteMapping("/{id}")
    public void deleteQuiz(@PathVariable Long id) {
        quizService.deleteQuiz(id);
    }
}
