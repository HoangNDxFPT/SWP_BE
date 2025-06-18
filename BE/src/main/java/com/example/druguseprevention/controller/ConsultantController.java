package com.example.druguseprevention.controller;

import com.example.druguseprevention.dto.*;
import com.example.druguseprevention.service.ConsultantService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
@SecurityRequirement(name = "api")
@SecurityRequirement(name = "bearer-key")
@RestController
@RequestMapping("/api/consultant")
@RequiredArgsConstructor
public class ConsultantController {

    private final ConsultantService consultantService;

    // ✅ Lấy userId từ token (username nằm trong token)
    private Long getCurrentUserId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return consultantService.getUserIdByUsername(username);
    }

    // CN01: Dashboard tổng quan
    @GetMapping("/dashboard")
    public ResponseEntity<ConsultantDashboardDto> getDashboard() {
        return ResponseEntity.ok(consultantService.getDashboard(getCurrentUserId()));
    }

    // CN02: Xem danh sách lịch hẹn
    @GetMapping("/appointments")
    public ResponseEntity<List<AppointmentDto>> getAppointments() {
        return ResponseEntity.ok(consultantService.getAppointments(getCurrentUserId()));
    }

    // CN02: Xác nhận cuộc hẹn
    @PutMapping("/appointments/{id}/confirm")
    public ResponseEntity<Void> confirmAppointment(@PathVariable Long id) {
        consultantService.confirmAppointment(id);
        return ResponseEntity.ok().build();
    }

    // CN02: Từ chối cuộc hẹn
    @PutMapping("/appointments/{id}/reject")
    public ResponseEntity<Void> rejectAppointment(@PathVariable Long id) {
        consultantService.rejectAppointment(id);
        return ResponseEntity.ok().build();
    }

    // CN04: Xem phân tích khảo sát của user
    @GetMapping("/survey-analysis/{userId}")
    public ResponseEntity<List<SurveyAnalysisDto>> getSurveyAnalysis(@PathVariable Long userId) {
        return ResponseEntity.ok(consultantService.getSurveyAnalysis(userId));
    }

    // CN03: Ghi chú hồ sơ người dùng
    @PutMapping("/user/{userId}/note")
    public ResponseEntity<Void> updateUserNote(@PathVariable Long userId, @RequestBody String note) {
        consultantService.updateUserNote(userId, note);
        return ResponseEntity.ok().build();
    }

    // CN05: Gợi ý hành động can thiệp
    @PostMapping("/user/{userId}/suggestion")
    public ResponseEntity<Void> suggestAction(
            @PathVariable Long userId,
            @RequestBody ConsultantSuggestionDto suggestionDto) {
        consultantService.suggestAction(userId, suggestionDto);
        return ResponseEntity.ok().build();
    }

    // CN06: Cập nhật hồ sơ tư vấn viên (dùng token)
    @PutMapping("/profile")
    public ResponseEntity<Void> updateConsultantProfile(@RequestBody ConsultantProfileDto profileDto) {
        consultantService.updateProfile(getCurrentUserId(), profileDto);
        return ResponseEntity.ok().build();
    }

    // CN07: Thống kê tư vấn (dùng token)
    @GetMapping("/statistics")
    public ResponseEntity<ConsultationStatisticsDto> getStatistics() {
        return ResponseEntity.ok(consultantService.getStatistics(getCurrentUserId()));
    }

    // CN02: Cập nhật trạng thái và ghi chú cuộc hẹn
    @PutMapping("/appointments/{id}/status")
    public ResponseEntity<Void> updateAppointmentStatus(
            @PathVariable Long id,
            @RequestBody UpdateAppointmentStatusDto statusDto) {
        consultantService.updateAppointmentStatus(id, statusDto);
        return ResponseEntity.ok().build();
    }
    // CN08: Tạo lịch hẹn với chuyên viên
    // CN08: Tạo lịch hẹn với chuyên viên
    @PostMapping("/appointments")
    public ResponseEntity<AppointmentCreatedResponseDto> createAppointment(
            @RequestBody CreateAppointmentDto dto) {
        Long consultantId = getCurrentUserId(); // từ token
        return ResponseEntity.ok(consultantService.createAppointment(consultantId, dto));
    }

    @PutMapping("/appointments/{id}/note")
    public ResponseEntity<Void> updateAppointmentNote(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String note = body.get("note");
        consultantService.updateAppointmentNote(id, note);
        return ResponseEntity.ok().build();
    }


}
