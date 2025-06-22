package com.example.druguseprevention.dto;

import com.example.druguseprevention.entity.Appointment;
import lombok.Data;

@Data
public class UpdateAppointmentStatusDto {
    private Appointment.Status status;
    private String note; // optional
}