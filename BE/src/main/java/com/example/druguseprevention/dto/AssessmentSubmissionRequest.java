package com.example.druguseprevention.dto;
import lombok.Data;

@Data
public class AssessmentSubmissionRequest {
    private Long questionId;
    private Long answerId;
    private Long substanceId; // Thêm field để track substance đã chọn
}
