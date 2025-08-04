package com.example.druguseprevention.controller;

import com.example.druguseprevention.entity.Substance;
import com.example.druguseprevention.service.SubstanceService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/substances")
@RequiredArgsConstructor
@SecurityRequirement(name = "api")
public class SubstanceController {

    private final SubstanceService substanceService;

    // Public endpoint - Lấy danh sách substances cho ASSIST assessment
    @GetMapping
    public ResponseEntity<List<Substance>> getAllActiveSubstances() {
        return ResponseEntity.ok(substanceService.getAllActiveSubstances());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Substance> getSubstanceById(@PathVariable Long id) {
        return substanceService.getSubstanceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
