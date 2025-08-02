package com.example.druguseprevention.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "course_quiz")
public class CourseQuiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    private String question;

    @Column(columnDefinition = "json")
    private String answer; // JSON string (e.g., ["A", "B", "C", "D"])
    private Integer correct; // index đáp án đúng (0-based)
}
