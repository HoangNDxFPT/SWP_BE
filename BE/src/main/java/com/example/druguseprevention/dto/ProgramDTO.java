package com.example.druguseprevention.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ProgramDTO {
    private Long id;
    private String name;
    private LocalDate start_date;
    private LocalDate end_date;
    private String location;
}
