package com.example.druguseprevention.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultantScheduleRequest {

    private Long consultantId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(type = "string", example = "2025-06-29")
    private LocalDate workDate;

    @JsonFormat(pattern = "HH:mm:ss")
    @Schema(type = "string", example = "08:00:00")
    private LocalTime startTime;

    @JsonFormat(pattern = "HH:mm:ss")
    @Schema(type = "string", example = "17:00:00")
    private LocalTime endTime;

    private Boolean isAvailable;

    private Integer maxAppointments;
}
