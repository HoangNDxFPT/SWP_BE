package com.example.druguseprevention.dto;

import lombok.Data;

import java.util.Map;

@Data
public class AssessmentDTO {
    private Long userId;
    private Long courseId;
    private Map<String, String> answers; // { "Q1": "A", "Q2": "C", ... }
}

