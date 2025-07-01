package com.example.druguseprevention.entity;


import com.example.druguseprevention.enums.SurveySendStatus;
import com.example.druguseprevention.enums.SurveyType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class SurveySendHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "program_id")
    private Program program;

    @ManyToOne
    @JoinColumn(name = "template_id")
    private SurveyTemplate template;

    @Enumerated(EnumType.STRING)
    private SurveyType templateType;

    @Column(columnDefinition = "TEXT")
    private String formUrl;

    private LocalDateTime sentAt;

    @Enumerated(EnumType.STRING)
    private SurveySendStatus status; // Sent or Failed

    @Column(columnDefinition = "TEXT")
    private String errorMessage;
}
