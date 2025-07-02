package com.example.druguseprevention.controller;

import com.example.druguseprevention.dto.SurveySendHistoryDTO;
import com.example.druguseprevention.service.SurveySendHistoryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/survey-history")
//@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "api")
public class SurveySendHistoryController {

    @Autowired
    private SurveySendHistoryService surveySendHistoryService;

    //  Lấy lịch sử gửi khảo sát của một người dùng
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SurveySendHistoryDTO>> getSurveyHistoryByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(surveySendHistoryService.getSurveyHistoryByUser(userId));
    }

    //  Lấy lịch sử gửi khảo sát của một chương trình
    @GetMapping("/program/{programId}")
    public ResponseEntity<List<SurveySendHistoryDTO>> getSurveyHistoryByProgram(@PathVariable Long programId) {
        return ResponseEntity.ok(surveySendHistoryService.getSurveyHistoryByProgram(programId));
    }
}
