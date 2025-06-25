package com.example.druguseprevention.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RecommendationCourseId implements Serializable {
    private Long assessmentResultId;
    private Long courseId;

    // equals() và hashCode() để đảm bảo hoạt động đúng
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RecommendationCourseId that)) return false;
        return Objects.equals(assessmentResultId, that.assessmentResultId) &&
                Objects.equals(courseId, that.courseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(assessmentResultId, courseId);
    }
}
