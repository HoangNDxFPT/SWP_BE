package com.example.druguseprevention.controller;

import com.example.druguseprevention.dto.AssessmentDTO;
import com.example.druguseprevention.service.AssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/assessments")
public class AssessmentController {

    @Autowired
    private AssessmentService assessmentService;

    @PostMapping("/submit")
    public ResponseEntity<?> submitAssessment(@RequestBody AssessmentDTO dto) {
        return ResponseEntity.ok(assessmentService.submitAssessment(dto));
    }
}
