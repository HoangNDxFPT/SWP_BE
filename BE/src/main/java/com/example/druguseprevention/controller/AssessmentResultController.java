package com.example.druguseprevention.controller;

import com.example.druguseprevention.dto.AssessmentResultResponse;
import com.example.druguseprevention.entity.User;
import com.example.druguseprevention.service.AssessmentResultService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/assessment-results")
@SecurityRequirement(name = "api")
public class AssessmentResultController {

    @Autowired
    AssessmentResultService assessmentResultService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('CONSULTANT')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AssessmentResultResponse>> getResultsByUserId(@PathVariable Long userId) {
        List<AssessmentResultResponse> responses = assessmentResultService.getResultsByUserId(userId);
        return ResponseEntity.ok(responses);
    }


    @GetMapping("/{resultId}")
    public ResponseEntity<AssessmentResultResponse> getResultById(@PathVariable Long resultId) {
        AssessmentResultResponse response = assessmentResultService.getResultById(resultId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<List<AssessmentResultResponse>> getMyAssessmentHistory(
            @AuthenticationPrincipal User currentUser) {
        Long userId = currentUser.getId();
        List<AssessmentResultResponse> responses = assessmentResultService.getResultsByUserId(userId);
        return ResponseEntity.ok(responses);
    }
}
