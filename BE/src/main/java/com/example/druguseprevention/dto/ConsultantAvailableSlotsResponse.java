package com.example.druguseprevention.dto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultantAvailableSlotsResponse {
    private Long consultantId;
    private String date;
    private List<TimeSlot> timeSlots;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TimeSlot {
        private String startTime; // "HH:mm"
        private String endTime;
        private Boolean available;
    }
}