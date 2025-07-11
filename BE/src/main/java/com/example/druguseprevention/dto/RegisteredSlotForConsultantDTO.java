package com.example.druguseprevention.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisteredSlotForConsultantDTO {
    private Long slotId;
    private LocalDate date;
    private String start;
    private String end;
    private String status;
    private String memberName;


}
