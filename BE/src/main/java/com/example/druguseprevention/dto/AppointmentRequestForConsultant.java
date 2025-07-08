package com.example.druguseprevention.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AppointmentRequestForConsultant {
    private Long userId;
    private Long slotId;
    private LocalDate appointmentDate;
}
