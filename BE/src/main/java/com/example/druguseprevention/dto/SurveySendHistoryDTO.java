package com.example.druguseprevention.dto;

import com.example.druguseprevention.enums.SurveySendStatus;
import com.example.druguseprevention.enums.SurveyType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SurveySendHistoryDTO {
    private Long id;
    private String userFullName;
    private String userEmail;
    private String programName;
    private String templateName;
    private SurveyType templateType;
    private String formUrl;
    private LocalDateTime sentAt;
    private SurveySendStatus status;
    private String errorMessage;
}
