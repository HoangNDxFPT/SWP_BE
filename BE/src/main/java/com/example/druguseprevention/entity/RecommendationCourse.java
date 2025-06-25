package com.example.druguseprevention.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationCourse {
    @EmbeddedId
    private RecommendationCourseId id;

    @ManyToOne
    @MapsId("assessmentResultId")
    @JoinColumn(name = "assessment_result_id")
    private AssessmentResult assessmentResult;

    @ManyToOne
    @MapsId("courseId")
    @JoinColumn(name="course_id")
    private Course course;
}
