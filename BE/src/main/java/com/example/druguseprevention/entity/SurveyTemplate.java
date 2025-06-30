package com.example.druguseprevention.entity;

import com.example.druguseprevention.enums.SurveyType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
public class SurveyTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private SurveyType type;

    private boolean isDeleted = false;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String googleFormUrl;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "template")
    @JsonIgnore
    private List<SurveySendHistory> surveySendHistories;
}
