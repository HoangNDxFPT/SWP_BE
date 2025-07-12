package com.example.druguseprevention.dto;

import com.example.druguseprevention.enums.ReportStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportResponse {
    private Long id;
    private String reason;
    private String description;
    private LocalDateTime createdAt;
    private ReportStatus status;
    private String adminNote;
    private String memberName;
    private Long appointmentId;
}
