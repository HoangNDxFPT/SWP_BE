package com.example.druguseprevention.controller;

import com.example.druguseprevention.entity.AssessmentQuestion;
import com.example.druguseprevention.enums.AssessmentType;
import com.example.druguseprevention.service.AssessmentQuestionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@SecurityRequirement(name = "api")

@RestController
@RequestMapping("/api/admin/assessment-questions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AssessmentQuestionController {

    private final AssessmentQuestionService questionService;

    // lấy tất cả bao gồm câu hỏi đã xóa và ch xóa
    @GetMapping
    public List<AssessmentQuestion> getAllQuestions() {
        return questionService.getAllQuestions();
    }

    // lấy các câu hỏi chưa xóa
    @GetMapping("/not-deleted")
    public List<AssessmentQuestion> getAllActiveQuestions() {
        return questionService.getAllNotDeletedQuestions();
    }


    @GetMapping("/{id}")
    public ResponseEntity<AssessmentQuestion> getQuestionById(@PathVariable Long id) {
        return questionService.getQuestionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AssessmentQuestion> createQuestion(@RequestBody AssessmentQuestion question) {
        return ResponseEntity.ok(questionService.createQuestion(question));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AssessmentQuestion> updateQuestion(@PathVariable Long id, @RequestBody AssessmentQuestion question) {
        try {
            return ResponseEntity.ok(questionService.updateQuestion(id, question));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }
}
