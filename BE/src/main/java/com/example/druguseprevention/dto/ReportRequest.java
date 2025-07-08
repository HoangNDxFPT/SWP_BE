package com.example.druguseprevention.dto;

import lombok.Data;

@Data
public class ReportRequest {
    long appointmentId;
    String reason;
    String description;
}
