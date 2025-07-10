package com.example.druguseprevention.controller;

import com.example.druguseprevention.dto.*;
import com.example.druguseprevention.entity.Appointment;
import com.example.druguseprevention.enums.AppointmentStatus;
import com.example.druguseprevention.service.AppointmentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@SecurityRequirement(name = "api")
@RequestMapping("/api/appointment")
public class AppointmentController {
    @Autowired
    private AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<AppointmentResponse> create(@RequestBody AppointmentRequest appointmentRequest) {
        AppointmentResponse appointment = appointmentService.create(appointmentRequest);
        return ResponseEntity.ok(appointment);
    }

    @PreAuthorize("hasRole('CONSULTANT')")
    @PostMapping("/consultant")
    public ResponseEntity<AppointmentResponseForConsultant> createByConsultant(@RequestBody AppointmentRequestForConsultant request) {
        AppointmentResponseForConsultant response = appointmentService.createByConsultant(request);
        return ResponseEntity.ok(response);
    }

    //  Consultant xem các lịch hẹn với member
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

    @DeleteMapping("/appointments/{appointmentId}")
    public ResponseEntity<Void> cancelAppointment(@PathVariable Long appointmentId) {
        appointmentService.cancelAppointment(appointmentId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('CONSULTANT')")
    @PutMapping("/consultant/status")
    public ResponseEntity<?> updateAppointmentStatusByConsultant(@RequestBody UpdateAppointmentStatusRequest request) {
        appointmentService.updateAppointmentStatus(request);
        return ResponseEntity.ok("Appointment status updated successfully");
    }
}