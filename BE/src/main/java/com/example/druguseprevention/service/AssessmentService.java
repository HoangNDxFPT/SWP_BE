package com.example.druguseprevention.service;

import com.example.druguseprevention.dto.AssessmentDTO;
import com.example.druguseprevention.entity.Assessment;

public interface AssessmentService {
    Assessment submitAssessment(AssessmentDTO dto);
}