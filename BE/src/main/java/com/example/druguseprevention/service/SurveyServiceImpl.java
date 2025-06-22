package com.example.druguseprevention.service;

import com.example.druguseprevention.dto.SurveyResultDto;
import com.example.druguseprevention.dto.SurveySuggestionDTo;
import com.example.druguseprevention.entity.SurveyResult;
import com.example.druguseprevention.repository.SurveyResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SurveyServiceImpl implements SurveyService {

    private final SurveyResultRepository repository;

    @Override
    public void saveSurveyResults(Long userId, List<SurveyResultDto> results) {
        List<SurveyResult> entities = results.stream().map(dto -> {
            SurveyResult entity = new SurveyResult();
            entity.setUserId(userId);
            entity.setQuestion(dto.getQuestion());
            entity.setAnswer(dto.getAnswer());
            entity.setSuggestion(generateSuggestion(dto.getAnswer()));
            entity.setSubmittedAt(LocalDateTime.now());
            return entity;
        }).collect(Collectors.toList());

        repository.saveAll(entities);
    }

    @Override
    public List<SurveySuggestionDTo> getSuggestionsByUserId(Long userId) {
        return repository.findByUserId(userId).stream().map(entity -> {
            SurveySuggestionDTo dto = new SurveySuggestionDTo();
            dto.setQuestion(entity.getQuestion());
            dto.setAnswer(entity.getAnswer());
            dto.setSuggestion(entity.getSuggestion());
            return dto;
        }).collect(Collectors.toList());
    }

    private String generateSuggestion(String answer) {
        if ("Có".equalsIgnoreCase(answer)) return "Tiếp tục duy trì hành vi tốt.";
        if ("Không".equalsIgnoreCase(answer)) return "Xem xét tư vấn với chuyên gia.";
        return "Đánh giá thêm.";
    }
}