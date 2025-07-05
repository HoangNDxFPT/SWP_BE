package com.example.druguseprevention.controller;

import com.example.druguseprevention.service.ConsultantService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@SecurityRequirement(name = "api")
@SecurityRequirement(name = "bearer-key")
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final ConsultantService consultantService;

    // ✅ Lấy userId từ token
    private Long getCurrentUserId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return consultantService.getUserIdByUsername(username);
    }

//    // ✅ API: Lấy danh sách cuộc hẹn của tôi
//    @GetMapping("/my-booking")
//    public ResponseEntity<List<AppointmentDTO>> getMyBookings() {
//        Long userId = getCurrentUserId();
//        List<AppointmentDTO> bookings = consultantService.getAppointmentsByUserId(userId);
//        return ResponseEntity.ok(bookings);
//    }
}
