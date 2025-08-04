package com.example.druguseprevention.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserAssessmentAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="assessment_result_id")
    private AssessmentResult assessmentResult;

    @ManyToOne
    @JoinColumn(name="question_id")
    private AssessmentQuestion question;

    @ManyToOne
    @JoinColumn(name="answer_id")
    private AssessmentAnswer answer;

    private LocalDateTime selectedAt;
}
