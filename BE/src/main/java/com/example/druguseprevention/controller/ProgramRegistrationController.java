package com.example.druguseprevention.controller;

import com.example.druguseprevention.service.ProgramRegistrationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/programs")
@SecurityRequirement(name = "api")
public class ProgramRegistrationController {

    @Autowired
    private ProgramRegistrationService registrationService;

    @PostMapping("/{programId}/register/{userId}")
    public ResponseEntity<?> register(@PathVariable Long programId) {
        registrationService.registerUserToProgram(programId);
        return ResponseEntity.ok("User registered successfully");
    }
}
