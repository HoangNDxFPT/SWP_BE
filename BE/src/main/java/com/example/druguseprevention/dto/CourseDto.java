package com.example.druguseprevention.dto;

import com.example.druguseprevention.entity.Course;
import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseDto {
    private Long id;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer durationInMinutes;
    private String url;

    public static CourseDto fromEntity(Course course) {
        return CourseDto.builder()
                .id(course.getId())
                .name(course.getName())
                .description(course.getDescription())
                .startDate(course.getStartDate())
                .endDate(course.getEndDate())
                .durationInMinutes(course.getDurationInMinutes())
                .url(course.getUrl())
                .build();
    }
}