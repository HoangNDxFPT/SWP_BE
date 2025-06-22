package com.example.druguseprevention.dto;
//(CN01)
import lombok.Data;

@Data
public class ConsultantDashboardDto {
    private int totalAppointments;
    private int confirmed;
    private int pending;
    private int rejected;
    private int completed;
}