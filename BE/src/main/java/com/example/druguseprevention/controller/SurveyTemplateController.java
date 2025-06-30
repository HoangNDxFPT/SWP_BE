package com.example.druguseprevention.controller;

import com.example.druguseprevention.entity.SurveyTemplate;
import com.example.druguseprevention.service.SurveyTemplateService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/survey-templates")
@SecurityRequirement(name = "api")
public class SurveyTemplateController {

    @Autowired
    private SurveyTemplateService surveyTemplateService;

    @GetMapping
    public List<SurveyTemplate> getAllTemplates() {
        return surveyTemplateService.getAllTemplates();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SurveyTemplate> getTemplateById(@PathVariable Long id) {
        return surveyTemplateService.getTemplateById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public SurveyTemplate createTemplate(@RequestBody SurveyTemplate template) {
        return surveyTemplateService.createTemplate(template);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SurveyTemplate> updateTemplate(@PathVariable Long id, @RequestBody SurveyTemplate updatedTemplate) {
        try {
            SurveyTemplate updated = surveyTemplateService.updateTemplate(id, updatedTemplate);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable Long id) {
        surveyTemplateService.deleteTemplate(id);
        return ResponseEntity.noContent().build();
    }
}
