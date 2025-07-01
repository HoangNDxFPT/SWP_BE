//package com.example.druguseprevention.dto;
//
//import com.fasterxml.jackson.annotation.JsonFormat;
//import io.swagger.v3.oas.annotations.media.Schema;
//import lombok.*;
//
//import java.time.LocalDate;
//import java.time.LocalTime;
//
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class ConsultantScheduleRequest {
//
//    private Long scheduleId;
//    private Long consultantId;
//    private String consultantName;
//    @JsonFormat(pattern = "yyyy-MM-dd")
//    @Schema(type = "string", example = "2025-06-29")
//    private LocalDate workDate;
//
//    // Sử dụng @JsonFormat để Jackson có thể phân tích chuỗi "HH:mm:ss" thành LocalTime
//    @JsonFormat(pattern = "HH:mm:ss")
//    @Schema(type = "string", example = "08:00:00")
//    private LocalTime startTime;
//
//    // Sử dụng @JsonFormat để Jackson có thể phân tích chuỗi "HH:mm:ss" thành LocalTime
//    @JsonFormat(pattern = "HH:mm:ss")
//    @Schema(type = "string", example = "17:00:00")
//    private LocalTime endTime;
//    private Integer maxAppointments;
//}
