package com.example.druguseprevention.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProgramParticipationDTO {
    private Long id;
    private LocalDateTime joinedAt;
    private String programName;
    private String userFullName;
    private String userEmail;
}
