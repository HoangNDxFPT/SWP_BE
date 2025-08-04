package com.example.druguseprevention.controller;

import com.example.druguseprevention.entity.User;
import com.example.druguseprevention.service.AssessmentResultService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/assessment-results")
@SecurityRequirement(name = "api")
@RequiredArgsConstructor
public class AssessmentResultController {

    private final AssessmentResultService assessmentResultService;

    // =============== Main Endpoints - Simplified ===============

    @GetMapping("/{resultId}")
    public ResponseEntity<Map<String, Object>> getCompleteResult(@PathVariable Long resultId) {
        Map<String, Object> response = assessmentResultService.getCompleteResult(resultId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<List<Map<String, Object>>> getMyResults(
            @AuthenticationPrincipal User currentUser) {
        Long userId = currentUser.getId();
        List<Map<String, Object>> responses = assessmentResultService.getMyResults(userId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/me/latest")
    public ResponseEntity<Map<String, Object>> getMyLatestResult(
            @AuthenticationPrincipal User currentUser) {
        Long userId = currentUser.getId();
        Map<String, Object> response = assessmentResultService.getMyLatestResult(userId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CONSULTANT')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getUserResults(@PathVariable Long userId) {
        List<Map<String, Object>> responses = assessmentResultService.getMyResults(userId);
        return ResponseEntity.ok(responses);
    }
}
