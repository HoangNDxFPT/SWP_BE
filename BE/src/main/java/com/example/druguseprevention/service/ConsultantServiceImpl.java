package com.example.druguseprevention.service;

import com.example.druguseprevention.dto.*;
import com.example.druguseprevention.entity.Appointment;
import com.example.druguseprevention.entity.ConsultantDetail;
import com.example.druguseprevention.entity.User;
import com.example.druguseprevention.enums.Role;
import com.example.druguseprevention.repository.AppointmentRepository;
import com.example.druguseprevention.repository.ConsultantDetailRepository;
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
    private final ConsultantDetailRepository consultantDetailRepository;

    @Override
    public ConsultantDashboardDto getDashboard(Long consultantId) {
        ConsultantDashboardDto dto = new ConsultantDashboardDto();
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

    @Override
    public void updateUserNote(Long userId, String note) {
        List<Appointment> appointments = appointmentRepository.findByUserId(userId);
        appointments.forEach(app -> {
            if (app.getStatus() != Appointment.Status.COMPLETED) {
                app.setNote(note);
            }
        });
        appointmentRepository.saveAll(appointments);
    }

    @Override
    public void suggestAction(Long userId, ConsultantSuggestionDto suggestionDto) {
        List<Appointment> appointments = appointmentRepository.findByUserId(userId);
        appointments.forEach(app -> {
            if (app.getStatus() == Appointment.Status.CONFIRMED) {
                app.setNote(suggestionDto.getSuggestion());
            }
        });
        appointmentRepository.saveAll(appointments);
    }

    // ✅ Cập nhật hồ sơ tư vấn viên
    @Override
    public void updateProfile(Long consultantId, ConsultantProfileDto dto) {
        User user = userRepository.findById(consultantId)
                .orElseThrow(() -> new RuntimeException("Consultant not found"));
        user.setFullName(dto.getFullName());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setAddress(dto.getAddress());
        userRepository.save(user);

        ConsultantDetail detail = consultantDetailRepository.findByConsultantId(consultantId);
        if (detail == null) {
            detail = new ConsultantDetail();
            detail.setConsultantId(consultantId);
            detail.setUser(user);
        }

        detail.setStatus(dto.getStatus());
        detail.setDegree(dto.getDegree());
        detail.setInformation(dto.getInformation());
        detail.setCertifiedDegree(dto.getCertifiedDegree()); // ✅ Thêm certifiedDegree
        consultantDetailRepository.save(detail);
    }

    @Override
    public ConsultantProfileDto getProfile(Long consultantId) {
        User user = (User) userRepository.findByIdAndDeletedFalse(consultantId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tư vấn viên"));

        ConsultantDetail detail = consultantDetailRepository.findByConsultantId(consultantId);

        ConsultantProfileDto dto = new ConsultantProfileDto();
        dto.setConsultantId(consultantId);
        dto.setFullName(user.getFullName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setAddress(user.getAddress());

        if (detail != null) {
            dto.setStatus(detail.getStatus());
            dto.setDegree(detail.getDegree());
            dto.setInformation(detail.getInformation());
            dto.setCertifiedDegree(detail.getCertifiedDegree()); // ✅ Lấy certifiedDegree
            dto.setCertifiedDegreeImage(detail.getCertifiedDegreeImage());
        }

        return dto;
    }

    @Override
    public ConsultationStatisticsDto getStatistics(Long consultantId) {
        List<Appointment> all = appointmentRepository.findByConsultantId(consultantId);
        long completed = all.stream().filter(a -> a.getStatus() == Appointment.Status.COMPLETED).count();

        ConsultationStatisticsDto dto = new ConsultationStatisticsDto();
        dto.setTotalAppointments(all.size());
        dto.setCompletedAppointments((int) completed);
        dto.setFeedbackCount((int) (completed * 0.8)); // giả định 80% có phản hồi
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

    @Override
    public AppointmentCreatedResponseDto createAppointment(Long consultantId, CreateAppointmentDto dto) {
        User consultant = userRepository.findById(consultantId)
                .orElseThrow(() -> new RuntimeException("Consultant not found"));

        User user = userRepository.findById((long) dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (dto.getFullName() != null && !dto.getFullName().isBlank()) {
            user.setFullName(dto.getFullName());
            userRepository.save(user);
        }

        Appointment appointment = Appointment.builder()
                .appointmentTime(dto.getAppointmentTime())
                .note(dto.getNote())
                .status(Appointment.Status.PENDING)
                .consultant(consultant)
                .user(user)
                .build();

        Appointment saved = appointmentRepository.save(appointment);

        AppointmentCreatedResponseDto response = new AppointmentCreatedResponseDto();
        response.setAppointmentId(saved.getId());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setAppointmentTime(saved.getAppointmentTime());
        response.setNote(saved.getNote());

        return response;
    }

    @Override
    public void updateAppointmentNote(Long appointmentId, String note) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        appointment.setNote(note);
        appointmentRepository.save(appointment);
    }

    @Override
    public List<UserProfileDto> getAllMemberProfiles() {
        return userRepository.findByRoleAndDeletedFalse(Role.MEMBER)
                .stream()
                .map(user -> {
                    UserProfileDto dto = new UserProfileDto();
                    dto.setId(user.getId());
                    dto.setEmail(user.getEmail());
                    dto.setFullName(user.getFullName());
                    dto.setPhoneNumber(user.getPhoneNumber());
                    dto.setAddress(user.getAddress());
                    dto.setDateOfBirth(user.getDateOfBirth());
                    dto.setGender(user.getGender());
                    dto.setRole(user.getRole());
                    return dto;
                })
                .collect(Collectors.toList());
    }
    @Override
    public List<AppointmentDto> getAppointmentsByUserId(Long userId) {
        return appointmentRepository.findByUserId(userId).stream().map(appointment -> {
            AppointmentDto dto = new AppointmentDto();
            dto.setId(appointment.getId());
            dto.setAppointmentTime(appointment.getAppointmentTime());
            dto.setStatus(appointment.getStatus());
            dto.setNote(appointment.getNote());

            if (appointment.getConsultant() != null) {
                dto.setConsultantFullName(appointment.getConsultant().getFullName());
                dto.setConsultantEmail(appointment.getConsultant().getEmail());
            }

            return dto;
        }).collect(Collectors.toList());
    }

}
