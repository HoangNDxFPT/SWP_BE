package com.example.druguseprevention.controller;

import com.example.druguseprevention.dto.ReportRequest;
import com.example.druguseprevention.dto.ReportResponse;
import com.example.druguseprevention.dto.UpdateReportRequest;
import com.example.druguseprevention.entity.Report;
import com.example.druguseprevention.service.ReportService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@SecurityRequirement(name = "api")
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<Report> createReport(ReportRequest reportRequest) {
        Report newReport = reportService.create(reportRequest);
        return ResponseEntity.ok(newReport);

    }

    // Member - Xem danh sách báo cáo của mình
    @GetMapping("/member/reports")
    public ResponseEntity<List<ReportResponse>> getMyReports() {
        return ResponseEntity.ok(reportService.getMyReports());
    }

    // Member - Xem chi tiết báo cáo của mình
    @GetMapping("/member/reports/{id}")
    public ResponseEntity<ReportResponse> getMyReport(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.getMyReportById(id));
    }

    // Admin - Xem tất cả báo cáo
    @GetMapping("/admin/reports")
    public ResponseEntity<List<ReportResponse>> getAllReports() {
        return ResponseEntity.ok(reportService.getAllReportsForAdmin());
    }

    // Admin - Xem chi tiết báo cáo
    @GetMapping("/admin/reports/{id}")
    public ResponseEntity<ReportResponse> getReportForAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.getReportByIdForAdmin(id));
    }

    // Admin - Cập nhật/trả lời báo cáo
    @PutMapping("/admin/reports/{id}")
    public ResponseEntity<ReportResponse> updateReport(@PathVariable Long id, @RequestBody UpdateReportRequest request) {
        return ResponseEntity.ok(reportService.updateReport(id, request));
    }
}
