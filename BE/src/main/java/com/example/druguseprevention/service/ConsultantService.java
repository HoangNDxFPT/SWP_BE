package com.example.druguseprevention.service;
import com.example.druguseprevention.dto.*;

import java.util.List;

public interface ConsultantService {
    ConsultantDashboardDto getDashboard(Long consultantId);
    List<AppointmentDto> getAppointments(Long consultantId);
    void confirmAppointment(Long id);
    void rejectAppointment(Long id);
    List<SurveyAnalysisDto> getSurveyAnalysis(Long userId);
    void updateUserNote(Long userId, String note);                         // CN03
    void suggestAction(Long userId, ConsultantSuggestionDto suggestion);  // CN05
    void updateProfile(Long consultantId, ConsultantProfileDto profile);  // CN06
    ConsultationStatisticsDto getStatistics(Long consultantId);           // CN07
    void updateAppointmentStatus(Long id, UpdateAppointmentStatusDto statusDto);

    Long getUserIdByUsername(String username);
}
