package com.example.druguseprevention.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

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
    @Column(name = "duration_in_minutes")
    private Integer durationInMinutes;


    @Enumerated(EnumType.STRING)
    private TargetAgeGroup targetAgeGroup;


    @Column(columnDefinition = "TEXT")
    private String url;

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted; // Cập nhật giá trị trường
    }

    public enum TargetAgeGroup {
        Teenagers,
        Adults
    }

    @Column(name = "is_deleted")
    private boolean isDeleted = false;



    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<RecommendationCourse> recommendationCourses;
}

