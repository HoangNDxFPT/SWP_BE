package com.example.druguseprevention.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
public class Program {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(columnDefinition="TEXT")
    private String description;
    private LocalDate start_date;
    private LocalDate end_date;
    private String location;
    private boolean isDeleted = false;

    @OneToMany(mappedBy = "program", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<ProgramParticipation> programParticipations;

    @OneToMany(mappedBy = "program", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<SurveySendHistory> surveySendHistories ;

    @OneToMany(mappedBy = "program", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<SurveyTemplate> surveyTemplates;
}
