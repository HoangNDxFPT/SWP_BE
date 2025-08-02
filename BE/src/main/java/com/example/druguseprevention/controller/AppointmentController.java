package com.example.druguseprevention.controller;

import com.example.druguseprevention.dto.*;
import com.example.druguseprevention.entity.Appointment;
import com.example.druguseprevention.enums.AppointmentStatus;
import com.example.druguseprevention.service.AppointmentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
@SecurityRequirement(name = "api")
@RequestMapping("/api/appointment")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    // tạo cuộc hẹn
    @PostMapping
    public ResponseEntity<AppointmentResponse> create(@RequestBody AppointmentRequest appointmentRequest) {
        AppointmentResponse appointment = appointmentService.create(appointmentRequest);
        return ResponseEntity.ok(appointment);
    }

    // consultant tạo cuộc hẹn
    @PreAuthorize("hasRole('CONSULTANT')")
    @PostMapping("/consultant")
    public ResponseEntity<AppointmentResponseForConsultant> createByConsultant(@RequestBody AppointmentRequestForConsultant request) {
        AppointmentResponseForConsultant response = appointmentService.createByConsultant(request);
        return ResponseEntity.ok(response);
    }

    //  Consultant xem các lịch hẹn của mình với member
    @PreAuthorize("hasRole('CONSULTANT')")
    @GetMapping("/appointments/consultant")
    public List<AppointmentResponseForConsultant> getAppointmentsForConsultantByStatus(@RequestParam AppointmentStatus status) {
        return appointmentService.getAppointmentsForConsultantByStatus(status);
    }

    // Member xem lịch của mình
    @GetMapping("/appointments")
    public List<AppointmentResponse> getAppointmentsByStatus(@RequestParam AppointmentStatus status) {
        return appointmentService.getMyAppointmentsByStatus(status);
    }

    // hủy cuộc hẹn
    @DeleteMapping("/appointments/{appointmentId}")
    public ResponseEntity<Void> cancelAppointment(@PathVariable Long appointmentId) {
        appointmentService.cancelAppointment(appointmentId);
        return ResponseEntity.noContent().build();
    }

    // cập nhật trạng thái cuộc hẹn
    @PreAuthorize("hasRole('CONSULTANT')")
    @PutMapping("/consultant/status")
    public ResponseEntity<?> updateAppointmentStatusByConsultant(@RequestBody UpdateAppointmentStatusRequest request) {
        appointmentService.updateAppointmentStatus(request);
        return ResponseEntity.ok("Appointment status updated successfully");
    }

    // Admin xem danh sách các cuộc hẹn, có bộ lọc
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/appointments/admin")
    public ResponseEntity<List<AppointmentAdminResponse>> searchAppointments(
            @RequestParam(required = false) AppointmentStatus status,
            @RequestParam(required = false) Long consultantId,
            @RequestParam(required = false) Long memberId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        AppointmentSearchRequest request = new AppointmentSearchRequest();
        request.setStatus(status);
        request.setConsultantId(consultantId);
        request.setMemberId(memberId);
        request.setDate(date);

        return ResponseEntity.ok(appointmentService.searchAppointments(request));
    }

    // Admin xem danh sách các cuộc hẹn của member có lọc theo ngày và trạng thái
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/appointments/admin/member/{memberId}")
    public ResponseEntity<List<AppointmentAdminResponse>> getAppointmentsOfMember(
            @PathVariable Long memberId,
            @RequestParam(required = false) AppointmentStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        AppointmentSearchRequest request = new AppointmentSearchRequest();
        request.setMemberId(memberId);
        request.setStatus(status);
        request.setStatus(status);
        return ResponseEntity.ok(appointmentService.searchAppointments(request));
    }
}