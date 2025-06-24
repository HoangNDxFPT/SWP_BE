package com.example.druguseprevention.dto;

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
    private LocalDate workDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Boolean isAvailable;
    private Integer maxAppointments;
}
