package com.example.druguseprevention.entity;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Data
public class CourseQuizResultDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String question;
    @Column(columnDefinition = "LONGTEXT")
    private String options; // e.g., "A.1;B.2;C.3"
    private String correctAnswer;
    private String studentAnswer;
    private boolean isCorrect;

    @ManyToOne
    @JoinColumn(name = "quiz_result_id", nullable = false)
    private CourseQuizResult quizResult;
}
