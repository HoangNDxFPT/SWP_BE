package com.example.druguseprevention.dto;

import com.example.druguseprevention.enums.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentResponse {
    private Long id;
    private LocalDate createAt;
    private AppointmentStatus status;
    private String consultantName;
    private LocalDate date;
    private String startTime;
    private String endTime;
    private String googleMeetLink;
}
