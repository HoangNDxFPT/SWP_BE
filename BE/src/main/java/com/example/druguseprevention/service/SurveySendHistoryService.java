package com.example.druguseprevention.service;

import com.example.druguseprevention.dto.SurveySendHistoryDTO;
import com.example.druguseprevention.entity.SurveySendHistory;
import com.example.druguseprevention.repository.SurveySendHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SurveySendHistoryService {

    private final SurveySendHistoryRepository surveySendHistoryRepository;

    private final ModelMapper modelMapper;

    public List<SurveySendHistoryDTO> getSurveyHistoryByUser(Long userId) {
        return surveySendHistoryRepository.findByUserId(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<SurveySendHistoryDTO> getSurveyHistoryByProgram(Long programId) {
        return surveySendHistoryRepository.findByProgramId(programId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private SurveySendHistoryDTO convertToDTO(SurveySendHistory history) {
        SurveySendHistoryDTO dto = new SurveySendHistoryDTO();
        dto.setId(history.getId());
        dto.setUserFullName(history.getUser().getFullName());
        dto.setUserEmail(history.getUser().getEmail());
        dto.setProgramName(history.getProgram().getName());
        dto.setTemplateName(history.getTemplate() != null ? history.getTemplate().getName() : null);
        dto.setTemplateType(history.getTemplateType());
        dto.setFormUrl(history.getFormUrl());
        dto.setSentAt(history.getSentAt());
        dto.setStatus(history.getStatus());
        dto.setErrorMessage(history.getErrorMessage());
        return dto;
    }
}
