package com.example.druguseprevention.service;
import com.example.druguseprevention.dto.SurveyResultDto;
import com.example.druguseprevention.dto.SurveySuggestionDTo;

import java.util.List;

public interface SurveyService {
    void saveSurveyResults(Long userId, List<SurveyResultDto> results);
    List<SurveySuggestionDTo> getSuggestionsByUserId(Long userId);
}