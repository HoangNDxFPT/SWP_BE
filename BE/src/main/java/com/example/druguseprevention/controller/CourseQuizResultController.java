
package com.example.druguseprevention.controller;

import com.example.druguseprevention.dto.*;
import com.example.druguseprevention.entity.CourseQuizResult;
import com.example.druguseprevention.entity.User;
import com.example.druguseprevention.repository.CourseQuizResultDetailRepository;
import com.example.druguseprevention.repository.CourseQuizResultRepository;
import com.example.druguseprevention.repository.UserRepository;
import com.example.druguseprevention.service.CourseQuizResultService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@SecurityRequirement(name = "api")
@SecurityRequirement(name = "bearer-key")
@RestController
@RequestMapping("/api/quiz-result")
@RequiredArgsConstructor
public class CourseQuizResultController {
    private final UserRepository userRepository;
    private final CourseQuizResultService service;
    private final CourseQuizResultRepository repository;
    private final CourseQuizResultDetailRepository courseQuizResultDetailRepository;
    private final CourseQuizResultService courseQuizResultService;

    @PostMapping("/submit/")
    public ResponseEntity<CourseQuizResultFullResponse> submitQuiz(
            @RequestBody QuizSubmitRequest request,
            @AuthenticationPrincipal User user) {

        CourseQuizResultFullResponse result = courseQuizResultService.submitQuizAndReturn(request, user);
        return ResponseEntity.ok(result);
    }



    // Chỉ Admin xem toàn bộ kết quả
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<CourseQuizResult> getAll() {
        return service.findAll();
    }

    // Admin
    @GetMapping("/{id}")
    public ResponseEntity<CourseQuizResult> getById(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(service.findById(id));
    }

    // Chỉ admin được cập nhật
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<CourseQuizResult> update(@PathVariable Long id, @RequestBody CourseQuizResult result) {
        return ResponseEntity.ok(service.update(id, result));
    }

    // Chỉ admin được xóa
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok("Deleted");
    }
    @GetMapping("/my-details")
    public ResponseEntity<List<CourseQuizResultDetailDto>> getMyQuizResultDetails(
            @RequestParam("resultId") Long resultId,
            Principal principal) {

        String username = principal.getName();
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Kiểm tra quyền sở hữu kết quả
        if (!service.isOwner(resultId, user.getId())) {
            return ResponseEntity.status(403).build(); // Forbidden
        }

        return ResponseEntity.ok(service.getResultDetailsByResultId(resultId));
    }

    //
//    @GetMapping("/my-results")
//    public ResponseEntity<List<CourseQuizResult>> getMyResults(Principal principal) {
//        String username = principal.getName();
//        User user = userRepository.findByUserName(username)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//        return ResponseEntity.ok(service.findByUserId(user.getId()));
//    }
    @GetMapping("/my-results")
    public ResponseEntity<?> getMyResults(Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<CourseQuizResultDto> results = service.getResultDtosByUserId(user.getId());
        if (results.isEmpty()) {
            return ResponseEntity.ok("Bạn chưa làm bài quiz nào.");
        }
        return ResponseEntity.ok(results);
    }

//    @PostMapping("/submit")
//    public ResponseEntity<?> submitQuiz(@RequestBody QuizSubmitRequest request, Principal principal) {
//        String username = principal.getName();
//        User user = userRepository.findByUserName(username)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        service.submitQuiz(request, user);
//
//        return ResponseEntity.ok("Đã lưu kết quả bài làm.");
//    }






}
