package com.example.druguseprevention.controller;

import com.example.druguseprevention.dto.CertificateDTO;
import com.example.druguseprevention.service.CertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/certificates")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CertificateDTO>> getCertificates(@PathVariable Long userId) {
        return ResponseEntity.ok(certificateService.getUserCertificates(userId));
    }

    @PostMapping("/generate")
    public ResponseEntity<CertificateDTO> generate(@RequestParam Long userId, @RequestParam Long courseId) {
        return ResponseEntity.ok(certificateService.generateCertificate(userId, courseId));
    }
}
