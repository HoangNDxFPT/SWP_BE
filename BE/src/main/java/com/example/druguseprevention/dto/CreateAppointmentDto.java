package com.example.druguseprevention.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CreateAppointmentDto {
    private int  userId;                  // người cần tư vấn
    private LocalDateTime appointmentTime;
    private String note;
}

