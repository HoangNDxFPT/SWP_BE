package com.example.druguseprevention.entity;

import com.example.druguseprevention.enums.AssessmentType;
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
public class AssessmentQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private AssessmentType assessmentType;

    @Column(columnDefinition="TEXT")
    private String questionText;

    private Integer questionOrder;

    private boolean isDeleted = false;

    // Thêm liên kết với Substance cho ASSIST
    @ManyToOne
    @JoinColumn(name = "substance_id")
    private Substance substance;

    @OneToMany(mappedBy = "question")
    @JsonIgnore
    private List<AssessmentAnswer> answers;

    @OneToMany(mappedBy = "question")
    @JsonIgnore
    private List<UserAssessmentAnswer> userAssessmentAnswers;
}
