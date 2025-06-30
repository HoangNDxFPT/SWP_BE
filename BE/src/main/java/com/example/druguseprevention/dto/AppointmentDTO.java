package com.example.druguseprevention.dto;
//CN02
import com.example.druguseprevention.entity.Appointment.Status;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentDTO {
    private Long id;
    private LocalDateTime appointmentTime;
    private Status status;
    private String note;
    private String userFullName;
    private String userEmail;
    private String consultantFullName;
    private String consultantEmail;

}