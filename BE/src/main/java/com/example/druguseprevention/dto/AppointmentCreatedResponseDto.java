package com.example.druguseprevention.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AppointmentCreatedResponseDto {
    private Long appointmentId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private LocalDateTime appointmentTime;
    private String note;
}
