
package com.example.druguseprevention.controller;

import com.example.druguseprevention.dto.CourseQuizResultDetailDto;
import com.example.druguseprevention.dto.CourseQuizResultDto;
import com.example.druguseprevention.dto.QuizSubmitRequest;
import com.example.druguseprevention.entity.CourseQuizResult;
import com.example.druguseprevention.entity.User;
import com.example.druguseprevention.repository.UserRepository;
import com.example.druguseprevention.service.CourseQuizResultService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.security.Principal;
import java.util.List;
@SecurityRequirement(name = "api")
@SecurityRequirement(name = "bearer-key")
@RestController
@RequestMapping("/api/quiz-result")
@RequiredArgsConstructor
public class CourseQuizResultController {
    private final UserRepository userRepository;
    private final CourseQuizResultService service;

    //  User và Admin đều được phép tạo
    @PostMapping
    public ResponseEntity<CourseQuizResult> create(@RequestBody CourseQuizResult result) {
        return ResponseEntity.ok(service.create(result));
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
    public ResponseEntity<List<CourseQuizResultDetailDto>> getMyQuizResultDetails(Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(service.getMyResultDetails(user.getId()));
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

    @PostMapping("/submit")
    public ResponseEntity<?> submitQuiz(@RequestBody QuizSubmitRequest request, Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        service.submitQuiz(request, user);

        return ResponseEntity.ok("Đã lưu kết quả bài làm.");
    }






}
