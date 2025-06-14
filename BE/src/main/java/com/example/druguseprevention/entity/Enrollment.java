package com.example.druguseprevention.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "enrollments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enrollment {

    @EmbeddedId
    private EnrollmentId id;

    @ManyToOne
    @MapsId("memberId")
    @JoinColumn(name = "member_id")
    private User member;

    @ManyToOne
    @MapsId("courseId")
    @JoinColumn(name = "course_id")
    private Course course;

    @Column(name = "enroll_date")
    private LocalDateTime enrollDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    // Getters and setters

    public enum Status {
        InProgress,
        Completed,
        Cancelled
    }
}
