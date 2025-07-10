package com.example.druguseprevention.controller;

import com.example.druguseprevention.dto.DashboardOverviewResponse;
import com.example.druguseprevention.service.DashboardService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
@SecurityRequirement(name = "api")
@SecurityRequirement(name = "bearer-key")
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/overview")
    public DashboardOverviewResponse getOverview() {
        return dashboardService.getOverview();
    }
}