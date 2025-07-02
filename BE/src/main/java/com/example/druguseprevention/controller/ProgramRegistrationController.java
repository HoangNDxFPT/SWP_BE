package com.example.druguseprevention.controller;

import com.example.druguseprevention.dto.ProgramDTO;
import com.example.druguseprevention.dto.ProgramParticipationDTO;
import com.example.druguseprevention.dto.UserDTO;
import com.example.druguseprevention.entity.ProgramParticipation;
import com.example.druguseprevention.service.ProgramRegistrationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/programs")
@SecurityRequirement(name = "api")
public class ProgramRegistrationController {

    @Autowired
    private ProgramRegistrationService registrationService;

    @PostMapping("/{programId}/register")
    public ResponseEntity<?> register(@PathVariable Long programId) {
        registrationService.registerUserToProgram(programId);
        return ResponseEntity.ok("User registered successfully");
    }

    @DeleteMapping("/{programId}/unregister")
    public ResponseEntity<String> unregisterFromProgram(@PathVariable Long programId) {
        registrationService.unregisterUserFromProgram(programId);
        return ResponseEntity.ok("Unregistered successfully");
    }

    @GetMapping("/my-history")
    public ResponseEntity<List<ProgramDTO>> getMyParticipationHistory() {
        return ResponseEntity.ok(registrationService.getParticipationHistoryForCurrentUser());
    }

    // Admin xem lịch sử tham gia của người dùng cụ thể
    @GetMapping("/history-user/{userId}")
    public ResponseEntity<List<ProgramDTO>> getUserParticipationHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(registrationService.getParticipationHistoryForUser(userId));
    }

    // Lịch sử tham gia theo chương trình
    @GetMapping("/history-program/{programId}")
    public ResponseEntity<List<UserDTO>> getParticipationByProgram(@PathVariable Long programId) {
        return ResponseEntity.ok(registrationService.getParticipationByProgram(programId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProgramParticipationDTO>> getAllParticipationHistory() {
        return ResponseEntity.ok(registrationService.getAllParticipationHistory());
    }
}
