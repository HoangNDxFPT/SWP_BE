package com.example.druguseprevention.controller;

import com.example.druguseprevention.entity.AssessmentAnswer;
import com.example.druguseprevention.service.AssessmentAnswerService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@SecurityRequirement(name = "api")

@RestController
@RequestMapping("/api/admin/assessment-answers")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AssessmentAnswerController {

    private final AssessmentAnswerService answerService;

    @GetMapping
    public List<AssessmentAnswer> getAllAnswers() {
        return answerService.getAllAnswers();
    }

    @GetMapping("/not-deleted")
    public List<AssessmentAnswer> getAllNotDeletedAnswers() {
        return answerService.getAllNotDeletedAnswers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssessmentAnswer> getAnswerById(@PathVariable Long id) {
        return answerService.getAnswerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AssessmentAnswer> createAnswer(@RequestBody AssessmentAnswer answer) {
        return ResponseEntity.ok(answerService.createAnswer(answer));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AssessmentAnswer> updateAnswer(@PathVariable Long id, @RequestBody AssessmentAnswer answer) {
        try {
            return ResponseEntity.ok(answerService.updateAnswer(id, answer));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDeleteAnswer(@PathVariable Long id) {
        answerService.softDeleteAnswer(id);
        return ResponseEntity.noContent().build();
    }
}
