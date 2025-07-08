package com.example.druguseprevention.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class AppointmentRequest {
    long slotId;
    long consultantId;
    LocalDate appointmentDate;

}

