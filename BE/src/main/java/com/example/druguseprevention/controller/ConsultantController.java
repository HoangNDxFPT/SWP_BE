package com.example.druguseprevention.controller;

import com.example.druguseprevention.dto.*;
import com.example.druguseprevention.service.ConsultantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/consultant")
@RequiredArgsConstructor
public class ConsultantController {

    private final ConsultantService consultantService;

    @GetMapping("/dashboard/{consultantId}")
    public ResponseEntity<ConsultantDashboardDto> getDashboard(@PathVariable Long consultantId) {
        return ResponseEntity.ok(consultantService.getDashboard(consultantId));
    }

    @GetMapping("/appointments/{consultantId}")
    public ResponseEntity<List<AppointmentDto>> getAppointments(@PathVariable Long consultantId) {
        return ResponseEntity.ok(consultantService.getAppointments(consultantId));
    }

    @PutMapping("/appointments/{id}/confirm")
    public ResponseEntity<Void> confirmAppointment(@PathVariable Long id) {
        consultantService.confirmAppointment(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/appointments/{id}/reject")
    public ResponseEntity<Void> rejectAppointment(@PathVariable Long id) {
        consultantService.rejectAppointment(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/survey-analysis/{userId}")
    public ResponseEntity<List<SurveyAnalysisDto>> getSurveyAnalysis(@PathVariable Long userId) {
        return ResponseEntity.ok(consultantService.getSurveyAnalysis(userId));
    }
    // CN03: Ghi chú hồ sơ người dùng
    @PutMapping("/user/{userId}/note")
    public ResponseEntity<Void> updateUserNote(
            @PathVariable Long userId,
            @RequestBody String note
    ) {
        consultantService.updateUserNote(userId, note);
        return ResponseEntity.ok().build();
    }

    // CN05: Gợi ý hành động can thiệp cho người dùng
    @PostMapping("/user/{userId}/suggestion")
    public ResponseEntity<Void> suggestAction(
            @PathVariable Long userId,
            @RequestBody ConsultantSuggestionDto suggestionDto
    ) {
        consultantService.suggestAction(userId, suggestionDto);
        return ResponseEntity.ok().build();
    }

    // CN06: Quản lý thông tin cá nhân & lịch làm việc (của chính consultant)
    @PutMapping("/profile/{consultantId}")
    public ResponseEntity<Void> updateConsultantProfile(
            @PathVariable Long consultantId,
            @RequestBody ConsultantProfileDto profileDto
    ) {
        consultantService.updateProfile(consultantId, profileDto); //
        return ResponseEntity.ok().build();
    }

    // CN07: Thống kê tư vấn & phản hồi
    @GetMapping("/statistics/{consultantId}")
    public ResponseEntity<ConsultationStatisticsDto> getStatistics(
            @PathVariable Long consultantId
    ) {
        return ResponseEntity.ok(consultantService.getStatistics(consultantId));
    }
    @PutMapping("/appointments/{id}/status")
    public ResponseEntity<Void> updateAppointmentStatus(
            @PathVariable Long id,
            @RequestBody UpdateAppointmentStatusDto statusDto
    ) {
        consultantService.updateAppointmentStatus(id, statusDto);
        return ResponseEntity.ok().build();
    }

}