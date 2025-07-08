package com.example.druguseprevention.controller;

import com.example.druguseprevention.dto.ReportRequest;
import com.example.druguseprevention.entity.Report;
import com.example.druguseprevention.service.ReportService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@SecurityRequirement(name = "api")
@RequestMapping("/api/report")
public class ReportController {
    @Autowired
    private ReportService reportService;

    @PostMapping
    public ResponseEntity createReport(ReportRequest reportRequest) {
        Report newReport = reportService.create(reportRequest);
        return ResponseEntity.ok(newReport);

    }
}
