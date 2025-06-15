package com.example.druguseprevention.controller;
import com.example.druguseprevention.dto.SurveyResultDto;
import com.example.druguseprevention.dto.SurveySuggestionDTo;
import com.example.druguseprevention.service.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/surveys")
@RequiredArgsConstructor
public class SurveyController {

    private final SurveyService surveyService;

    // M_SV03
    @GetMapping("/results/{userId}")
    public ResponseEntity<List<SurveySuggestionDTo>> getSurveyResults(@PathVariable Long userId) {
        return ResponseEntity.ok(surveyService.getSuggestionsByUserId(userId));
    }

    // M_SV04
    @PostMapping("/submit/{userId}")
    public ResponseEntity<String> submitSurvey(@PathVariable Long userId, @RequestBody List<SurveyResultDto> results) {
        surveyService.saveSurveyResults(userId, results);
        return ResponseEntity.ok("Survey submitted successfully.");
    }
}