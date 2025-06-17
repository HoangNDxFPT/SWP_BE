package com.example.druguseprevention.service;
import com.example.druguseprevention.dto.*;
import com.example.druguseprevention.entity.Appointment;
import com.example.druguseprevention.entity.User;
import com.example.druguseprevention.repository.AppointmentRepository;
import com.example.druguseprevention.repository.SurveyResultRepository;
import com.example.druguseprevention.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConsultantServiceImpl implements ConsultantService {

    private final AppointmentRepository appointmentRepository;
    private final SurveyResultRepository surveyResultRepository;
    private final UserRepository userRepository;

    @Override
    public ConsultantDashboardDto getDashboard(Long consultantId) {
        ConsultantDashboardDto dto = new ConsultantDashboardDto();
        dto.setTotalAppointments(appointmentRepository.findByConsultantId(consultantId).size());
        dto.setTotalAppointments(appointmentRepository.findByConsultantId(consultantId).size());
        dto.setConfirmed((int) appointmentRepository.countByConsultantIdAndStatus(consultantId, Appointment.Status.CONFIRMED));
        dto.setPending((int) appointmentRepository.countByConsultantIdAndStatus(consultantId, Appointment.Status.PENDING));
        dto.setRejected((int) appointmentRepository.countByConsultantIdAndStatus(consultantId, Appointment.Status.REJECTED));
        dto.setCompleted((int) appointmentRepository.countByConsultantIdAndStatus(consultantId, Appointment.Status.COMPLETED));

        return dto;
    }

    @Override
    public List<AppointmentDto> getAppointments(Long consultantId) {
        return appointmentRepository.findByConsultantId(consultantId).stream().map(appointment -> {
            AppointmentDto dto = new AppointmentDto();
            dto.setId(appointment.getId());
            dto.setAppointmentTime(appointment.getAppointmentTime());
            dto.setStatus(appointment.getStatus());
            dto.setNote(appointment.getNote());
            dto.setUserFullName(appointment.getUser().getFullName());
            dto.setUserEmail(appointment.getUser().getEmail());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public void confirmAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        appointment.setStatus(Appointment.Status.CONFIRMED);
        appointmentRepository.save(appointment);
    }

    @Override
    public void rejectAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        appointment.setStatus(Appointment.Status.REJECTED);
        appointmentRepository.save(appointment);
    }

    @Override
    public List<SurveyAnalysisDto> getSurveyAnalysis(Long userId) {
        return surveyResultRepository.findByUserId(userId).stream().map(result -> {
            SurveyAnalysisDto dto = new SurveyAnalysisDto();
            dto.setQuestion(result.getQuestion());
            dto.setAnswer(result.getAnswer());
            dto.setSuggestion(result.getSuggestion());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override // CN03: Ghi chú vào hồ sơ người dùng (ví dụ lưu vào note của các appointment chưa completed)
    public void updateUserNote(Long userId, String note) {
        List<Appointment> appointments = appointmentRepository.findByUserId(userId);
        appointments.forEach(app -> {
            if (app.getStatus() != Appointment.Status.COMPLETED) {
                app.setNote(note);
            }
        });
        appointmentRepository.saveAll(appointments);
    }

    @Override // CN05: Gợi ý hành động can thiệp (giả định lưu như một appointment note)
    public void suggestAction(Long userId, ConsultantSuggestionDto suggestionDto) {
        List<Appointment> appointments = appointmentRepository.findByUserId(userId);
        appointments.forEach(app -> {
            if (app.getStatus() == Appointment.Status.CONFIRMED) {
                app.setNote(suggestionDto.getSuggestion());
            }
        });
        appointmentRepository.saveAll(appointments);
    }

    @Override // CN06: Cập nhật hồ sơ cá nhân & thời gian làm việc
    public void updateProfile(Long consultantId, ConsultantProfileDto profileDto) {
        User consultant = userRepository.findById(consultantId)
                .orElseThrow(() -> new RuntimeException("Consultant not found"));
        consultant.setFullName(profileDto.getFullName());
        consultant.setPhoneNumber(profileDto.getPhoneNumber());
        consultant.setAddress(profileDto.getAddress());
        userRepository.save(consultant);
    }

    @Override // CN07: Thống kê tư vấn & phản hồi
    public ConsultationStatisticsDto getStatistics(Long consultantId) {
        List<Appointment> all = appointmentRepository.findByConsultantId(consultantId);
        long completed = all.stream().filter(a -> a.getStatus() == Appointment.Status.COMPLETED).count();

        ConsultationStatisticsDto dto = new ConsultationStatisticsDto();
        dto.setTotalAppointments(all.size());
        dto.setCompletedAppointments(completed);
        dto.setFeedbackCount((int) (completed * 0.8)); // giả định 80% có feedback
        return dto;
    }

    @Override
    public void updateAppointmentStatus(Long id, UpdateAppointmentStatusDto statusDto) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appointment.setStatus(statusDto.getStatus());

        if (statusDto.getNote() != null && !statusDto.getNote().isBlank()) {
            appointment.setNote(statusDto.getNote());
        }

        appointmentRepository.save(appointment);
    }

    @Override
    public Long getUserIdByUsername(String username) {
        return userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
    }

}