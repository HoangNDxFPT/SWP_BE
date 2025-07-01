package com.example.druguseprevention.service;

import com.example.druguseprevention.entity.SurveyTemplate;
import com.example.druguseprevention.repository.SurveyTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SurveyTemplateService {

    @Autowired
    SurveyTemplateRepository surveyTemplateRepository;

    public List<SurveyTemplate> getAllTemplates() {
        return surveyTemplateRepository.findByIsDeletedFalse();
    }

    public Optional<SurveyTemplate> getTemplateById(Long id) {
        return surveyTemplateRepository.findByIdAndIsDeletedFalse(id);
    }

    public SurveyTemplate createTemplate(SurveyTemplate template) {
        template.setDeleted(false);
        template.setCreatedAt(LocalDateTime.now());
        return surveyTemplateRepository.save(template);
    }

    public SurveyTemplate updateTemplate(Long id, SurveyTemplate updatedTemplate) {
        return surveyTemplateRepository.findByIdAndIsDeletedFalse(id)
                .map(template -> {
                    template.setName(updatedTemplate.getName());
                    template.setType(updatedTemplate.getType());
                    template.setDescription(updatedTemplate.getDescription());
                    template.setGoogleFormUrl(updatedTemplate.getGoogleFormUrl());
                    return surveyTemplateRepository.save(template);
                })
                .orElseThrow(() -> new RuntimeException("Survey Template not found or has been deleted"));
    }

    public void deleteTemplate(Long id) {
        surveyTemplateRepository.findByIdAndIsDeletedFalse(id).ifPresent(template -> {
            template.setDeleted(true);
            surveyTemplateRepository.save(template);
        });
    }
}
