package com.example.druguseprevention.service;

import com.example.druguseprevention.dto.*;

import java.time.LocalDate;
import java.util.List;

public interface ConsultantService {
    ConsultantDashboardDto getDashboard(Long consultantId);
    List<AppointmentDTO> getAppointments(Long consultantId);
    void confirmAppointment(Long id);
    void rejectAppointment(Long id);
    void updateUserNote(Long userId, String note);                         // CN03
    void suggestAction(Long userId, ConsultantSuggestionDto suggestion);  // CN05
    void updateProfile(Long consultantId, ConsultantProfileDto profile);  // CN06
    ConsultationStatisticsDto getStatistics(Long consultantId);           // CN07
    void updateAppointmentStatus(Long id, UpdateAppointmentStatusDto statusDto);

    Long getUserIdByUsername(String username);
    AppointmentCreatedResponseDto createAppointment(Long consultantId, CreateAppointmentDto dto);
    void updateAppointmentNote(Long appointmentId, String note);  // CN09

    List<UserProfileDto> getAllMemberProfiles();
    ConsultantProfileDto getProfile(Long consultantId);
    List<AppointmentDTO> getAppointmentsByUserId(Long userId);
    ConsultantAvailableSlotsResponse getAvailableSlots(Long consultantId, LocalDate date);
    ConsultantPublicProfileDto getPublicConsultantProfile(Long consultantId);

//    List<ConsultantPublicProfileDto> getAllPublicConsultantProfiles();
}
