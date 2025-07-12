package com.example.druguseprevention.dto;

import com.example.druguseprevention.enums.ReportStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateReportRequest {
    private ReportStatus status;
    private String adminNote;
}
