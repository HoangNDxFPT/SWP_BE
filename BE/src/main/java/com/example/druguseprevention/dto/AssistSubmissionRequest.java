package com.example.druguseprevention.dto;

import lombok.Data;
import java.util.List;

@Data
public class AssistSubmissionRequest {
    // Multiple substances - mỗi substance sẽ có bộ câu hỏi Q2-Q8 (bao gồm cả injection)
    private List<SubstanceAssessment> substanceAssessments;

    @Data
    public static class SubstanceAssessment {
        private Long substanceId;
        private List<Long> answerIds; // Q2, Q3, Q4, Q5, Q6, Q7, Q8 theo thứ tự cho substance này
    }
}
