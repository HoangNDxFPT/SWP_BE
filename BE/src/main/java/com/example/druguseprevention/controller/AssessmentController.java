package com.example.druguseprevention.controller;

import com.example.druguseprevention.dto.AssessmentResultResponse;
import com.example.druguseprevention.dto.AssessmentStartResponse;
import com.example.druguseprevention.dto.AssessmentSubmissionRequest;
import com.example.druguseprevention.dto.AssistSubmissionRequest;
import com.example.druguseprevention.dto.AssistResultResponse;
import com.example.druguseprevention.dto.AssistStartResponse;
import com.example.druguseprevention.entity.Assessment;
import com.example.druguseprevention.entity.AssessmentQuestion;
import com.example.druguseprevention.entity.AssessmentResult;
import com.example.druguseprevention.enums.AssessmentType;
import com.example.druguseprevention.service.AssessmentService;
import com.example.druguseprevention.service.AssistService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assessments")
@RequiredArgsConstructor
@SecurityRequirement(name = "api")
public class AssessmentController {

    private final AssessmentService assessmentService;
    private final AssistService assistService;



    @GetMapping("/questions/{type}")
    public ResponseEntity<List<AssessmentQuestion>> getQuestions(@PathVariable AssessmentType type) {
        return ResponseEntity.ok(assessmentService.getQuestionsByType(type));
    }

    //  Bắt đầu bài đánh giá

    @PostMapping("/start")
    public ResponseEntity<AssessmentStartResponse> startAssessment(@RequestParam AssessmentType type) {
        return ResponseEntity.ok(assessmentService.startAssessment(type));
    }

    @PostMapping("/start-assist")
    public ResponseEntity<AssistStartResponse> startAssistAssessment() {
        AssistStartResponse response = assistService.startAssistAssessment();
        return ResponseEntity.ok(response);
    }

    //  Lấy bài đánh giá gần nhất của người dùng hiện tại
    @GetMapping("/my-latest")
    public Assessment getMyLatestAssessment() {
        return assessmentService.getMyLatestAssessment();
    }

    //  Xem bài đánh giá cụ thể theo ID
    @GetMapping("/{id}")
    public Assessment getAssessmentById(@PathVariable Long id) {
        return assessmentService.getAssessmentById(id);
    }

    //  Lịch sử đánh giá của người dùng hiện tại
    @GetMapping("/my-history")
    public List<Assessment> getMyHistory() {
        return assessmentService.getMyHistory();
    }

    //  Admin xem toàn bộ lịch sử đánh giá của mọi người
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CONSULTANT')")
    public List<Assessment> getAllAssessments() {
        return assessmentService.getAllAssessments();
    }



    @PostMapping("/submit")
    public ResponseEntity<AssessmentResultResponse> submitAssessment(
            @RequestParam AssessmentType type,
            @RequestBody List<AssessmentSubmissionRequest> submissionRequests) {

        AssessmentResultResponse response = assessmentService.submit(type, submissionRequests);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/submit-assist")
    public ResponseEntity<AssistResultResponse> submitAssistAssessment(
            @RequestBody AssistSubmissionRequest assistRequest) {

        AssistResultResponse response = assistService.submitAssistAssessment(assistRequest);
        return ResponseEntity.ok(response);
    }

    // Xem kết quả ASSIST assessment theo ID
    @GetMapping("/assist-result/{assessmentResultId}")
    public ResponseEntity<AssistResultResponse> getAssistResult(@PathVariable Long assessmentResultId) {
        AssistResultResponse response = assistService.getAssistResult(assessmentResultId);
        return ResponseEntity.ok(response);
    }
}
