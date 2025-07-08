package com.example.druguseprevention.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegisteredSlotDTO {
    private Long slotId;
    private String label;
    private String startTime;
    private String endTime;
    private LocalDate date;
    private boolean isAvailable;
}
