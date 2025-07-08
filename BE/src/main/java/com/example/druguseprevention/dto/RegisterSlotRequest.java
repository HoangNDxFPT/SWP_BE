package com.example.druguseprevention.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterSlotRequest {
    private LocalDate date;
}
