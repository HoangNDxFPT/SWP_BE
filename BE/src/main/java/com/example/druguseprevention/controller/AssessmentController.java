package com.example.druguseprevention.controller;

import com.example.druguseprevention.dto.AssessmentResultResponse;
import com.example.druguseprevention.dto.AssessmentStartResponse;
import com.example.druguseprevention.dto.AssessmentSubmissionRequest;
import com.example.druguseprevention.entity.Assessment;
import com.example.druguseprevention.entity.AssessmentQuestion;
import com.example.druguseprevention.entity.AssessmentResult;
import com.example.druguseprevention.enums.AssessmentType;
import com.example.druguseprevention.service.AssessmentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
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

    /**
     * Lấy danh sách câu hỏi cho loại đánh giá (CRAFFT / ASSIST)
     */

    @GetMapping("/questions/{type}")
    public ResponseEntity<List<AssessmentQuestion>> getQuestions(@PathVariable AssessmentType type) {
        return ResponseEntity.ok(assessmentService.getQuestionsByType(type));
    }

    //  Bắt đầu bài đánh giá

    @PostMapping("/start")
    public ResponseEntity<AssessmentStartResponse> startAssessment(@RequestParam AssessmentType type) {
        return ResponseEntity.ok(assessmentService.startAssessment(type));
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
    @PreAuthorize("hasRole('ADMIN')")
    public List<Assessment> getAllAssessments() {
        return assessmentService.getAllAssessments();
    }

    /**
     * Người dùng gửi kết quả đánh giá
     */

    @PostMapping("/submit")
    public ResponseEntity<AssessmentResultResponse> submitAssessment(
            @RequestParam Long assessmentId,
            @RequestBody List<AssessmentSubmissionRequest> submissionRequests) {

        AssessmentResultResponse response = assessmentService.submit(assessmentId, submissionRequests);
        return ResponseEntity.ok(response);
    }
}
