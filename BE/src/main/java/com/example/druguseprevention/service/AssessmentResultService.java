package com.example.druguseprevention.service;

import com.example.druguseprevention.dto.AssessmentResultResponse;
import com.example.druguseprevention.entity.AssessmentResult;
import com.example.druguseprevention.exception.exceptions.BadRequestException;
import com.example.druguseprevention.repository.AssessmentResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssessmentResultService {
    @Autowired
    AssessmentResultRepository resultRepository;
    @Autowired
    AssessmentService assessmentService;

    public AssessmentResultResponse getResultById(Long id) {
        AssessmentResult result = resultRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Assessment result not found: " + id));
        return assessmentService.convertToResponse(result);
    }

    public List<AssessmentResultResponse> getResultsByUserId(Long userId) {
        List<AssessmentResult> results = resultRepository.findByAssessmentMemberId(userId);
        return results.stream()
                .map(assessmentService::convertToResponse) // Dùng lại hàm từ AssessmentService
                .toList();
    }
}
