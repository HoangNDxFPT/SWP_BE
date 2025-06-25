package com.example.druguseprevention.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="question_id", nullable=false)
    private AssessmentQuestion question;

    @Column(columnDefinition="TEXT")
    private String answerText;
    private Integer score;
    private  boolean isDeleted = false;

    @OneToMany(mappedBy = "answer", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<UserAssessmentAnswer> userAssessmentAnswers;
}
