package com.example.druguseprevention.dto;

import com.example.druguseprevention.enums.AppointmentStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AppointmentSearchRequest {
    private AppointmentStatus status;
    private Long consultantId;
    private Long memberId;
    private LocalDate date;
}
