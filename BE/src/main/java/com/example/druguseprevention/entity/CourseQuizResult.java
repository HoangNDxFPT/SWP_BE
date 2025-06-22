package com.example.druguseprevention.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class CourseQuizResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int score;
    private int totalQuestions;

    @ManyToOne
    private User user;

    @ManyToOne
    private Course course;

    private LocalDateTime submittedAt = LocalDateTime.now();
}