package com.example.druguseprevention.controller;

import com.example.druguseprevention.enums.SurveyType;
import com.example.druguseprevention.service.SurveyMailService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/surveys")
@SecurityRequirement(name = "api")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class SurveyMailController {

    private final SurveyMailService surveyMailService;

    @PostMapping("/send/{programId}/{type}")
    public ResponseEntity<?> sendSurvey(@PathVariable Long programId, @PathVariable SurveyType type) {
        surveyMailService.sendSurveyToParticipants(programId, type);
        return ResponseEntity.ok("Survey emails sent");
    }
}
