package com.example.druguseprevention.dto;

import lombok.Data;

@Data
public class UpdateAppointmentActionDTO {
    private String action; // CONFIRM, REJECT, SET_STATUS, ADD_NOTE
    private String status; // optional
    private String note;   // optional
}

