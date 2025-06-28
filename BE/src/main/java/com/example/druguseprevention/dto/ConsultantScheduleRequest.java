package com.example.druguseprevention.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    private LocalDate workDate;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startTime;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime endTime;

    private Boolean isAvailable;

    private Integer maxAppointments;
}
