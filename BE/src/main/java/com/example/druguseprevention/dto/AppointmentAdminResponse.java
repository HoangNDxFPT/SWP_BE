package com.example.druguseprevention.dto;

import com.example.druguseprevention.enums.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class AppointmentAdminResponse {
    private Long id;
    private LocalDate createAt;
    private AppointmentStatus status;
    private String memberName;
    private String consultantName;
    private LocalDate date;
    private String startTime;
    private String endTime;
}
