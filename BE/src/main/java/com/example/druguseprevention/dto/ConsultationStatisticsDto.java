package com.example.druguseprevention.dto;

import lombok.Data;

@Data
public class ConsultationStatisticsDto {
    private long totalAppointments;
    private long completedAppointments;
    private int feedbackCount;
}