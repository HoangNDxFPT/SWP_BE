package com.example.druguseprevention.dto;

import lombok.Data;

@Data
public class ConsultationStatisticsDto {
    private int totalAppointments;
    private int completedAppointments;
    private int feedbackCount;
}