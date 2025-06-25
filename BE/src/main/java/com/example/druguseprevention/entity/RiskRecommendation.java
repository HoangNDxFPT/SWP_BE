package com.example.druguseprevention.entity;

import com.example.druguseprevention.enums.RecommendationAction;
import com.example.druguseprevention.enums.RiskLevel;
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
public class RiskRecommendation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel;
    @Enumerated(EnumType.STRING)
    private RecommendationAction action;
    @Column(columnDefinition="TEXT")
    private String message;

    private boolean isDeleted = false;

    @OneToMany(mappedBy ="recommendation" )
    @JsonIgnore
    List<AssessmentResult> results;
}
