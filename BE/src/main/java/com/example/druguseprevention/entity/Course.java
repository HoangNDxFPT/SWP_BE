package com.example.druguseprevention.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDate startDate;

    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private TargetAgeGroup targetAgeGroup;

    private String type;

    @Column(columnDefinition = "TEXT")
    private String url;

    public enum TargetAgeGroup {
        Teenagers,
        Adults
    }
}