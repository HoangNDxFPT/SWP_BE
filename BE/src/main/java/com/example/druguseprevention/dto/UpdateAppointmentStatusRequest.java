package com.example.druguseprevention.dto;

import com.example.druguseprevention.enums.AppointmentStatus;
import lombok.Data;

@Data
public class UpdateAppointmentStatusRequest {
    private Long appointmentId;
    private AppointmentStatus status;
}
