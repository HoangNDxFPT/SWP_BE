package com.example.druguseprevention.entity;

import com.example.druguseprevention.enums.ReportStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String reason;
    private String description;
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private ReportStatus status = ReportStatus.PENDING; // Default status
    private String adminNote;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User member;

    @ManyToOne
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

}
