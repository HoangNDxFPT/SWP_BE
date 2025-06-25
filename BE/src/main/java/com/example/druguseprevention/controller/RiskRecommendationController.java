package com.example.druguseprevention.controller.admin;

import com.example.druguseprevention.entity.RiskRecommendation;
import com.example.druguseprevention.enums.RiskLevel;
import com.example.druguseprevention.service.RiskRecommendationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "api")

@RestController
@RequestMapping("/api/admin/risk-recommendations")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class RiskRecommendationController {

    private final RiskRecommendationService recommendationService;

    @GetMapping
    public List<RiskRecommendation> getAll() {
        return recommendationService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RiskRecommendation> getById(@PathVariable Long id) {
        return recommendationService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public RiskRecommendation create(@RequestBody RiskRecommendation recommendation) {
        return recommendationService.create(recommendation);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RiskRecommendation> update(@PathVariable Long id, @RequestBody RiskRecommendation updated) {
        return recommendationService.update(id, updated)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (recommendationService.delete(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}