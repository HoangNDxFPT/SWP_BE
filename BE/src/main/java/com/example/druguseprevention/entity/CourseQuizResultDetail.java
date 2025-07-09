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
    private String options; // JSON string nếu có nhiều lựa chọn
    private String correctAnswer;
    private String studentAnswer;
    private boolean isCorrect;

    @ManyToOne
    private CourseQuizResult quizResult;
}
