package com.example.druguseprevention.entity;

import com.example.druguseprevention.enums.RiskLevel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="assessment_id")
    private Assessment assessment;

    private Integer score;

    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel;

    @ManyToOne
    @JoinColumn(name="risk_recommendation_id")
    private RiskRecommendation recommendation;

    private LocalDateTime dateTaken;
    @OneToMany(mappedBy = "assessmentResult")
    @JsonIgnore
    private List<UserAssessmentAnswer> userAnswers;

}

