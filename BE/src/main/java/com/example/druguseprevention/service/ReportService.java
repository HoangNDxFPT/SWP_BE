package com.example.druguseprevention.service;

import com.example.druguseprevention.dto.ReportRequest;
import com.example.druguseprevention.dto.ReportResponse;
import com.example.druguseprevention.dto.UpdateReportRequest;
import com.example.druguseprevention.entity.Appointment;
import com.example.druguseprevention.entity.Report;
import com.example.druguseprevention.entity.User;
import com.example.druguseprevention.enums.ReportStatus;
import com.example.druguseprevention.exception.exceptions.BadRequestException;
import com.example.druguseprevention.repository.AppointmentRepository;
import com.example.druguseprevention.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;

    private final AuthenticationService authenticationService;

    private final AppointmentRepository appointmentRepository;

    private final UserService userService;

    private final ModelMapper modelMapper;

    public Report create(ReportRequest reportRequest) {
        User currentMember = userService.getCurrentUser();
        Appointment appointment = appointmentRepository.findById(reportRequest.getAppointmentId())
                .orElseThrow(() -> new BadRequestException("Appointment not found"));

        if (!appointment.getMember().getId().equals(currentMember.getId())) {
            throw new BadRequestException("This appointment does not belong to you");
        }

        Report report = new Report();
        report.setAppointment(appointment);
        report.setMember(currentMember);
        report.setReason(reportRequest.getReason());
        report.setDescription(reportRequest.getDescription());
        report.setCreatedAt(LocalDateTime.now());
        report.setStatus(ReportStatus.PENDING);

        return reportRepository.save(report);
    }

    public ReportResponse updateReport(Long reportId, UpdateReportRequest request) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new BadRequestException("Không tìm thấy báo cáo"));

        report.setStatus(request.getStatus());
        report.setAdminNote(request.getAdminNote());

        return mapToResponse(reportRepository.save(report));
    }

    public List<ReportResponse> getMyReports() {
        User member = userService.getCurrentUser();
        return reportRepository.findByMember(member).stream().map(this::mapToResponse).toList();
    }

    public ReportResponse getMyReportById(Long id) {
        User user = userService.getCurrentUser();
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Không tìm thấy báo cáo"));
        if (!report.getMember().getId().equals(user.getId())) {
            throw new BadRequestException("Bạn không có quyền truy cập báo cáo này");
        }
        return mapToResponse(report);
    }

    public List<ReportResponse> getAllReportsForAdmin() {
        return reportRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    public ReportResponse getReportByIdForAdmin(Long id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Không tìm thấy báo cáo"));
        return mapToResponse(report);
    }


    private ReportResponse mapToResponse (Report report){
        ReportResponse reportResponse = modelMapper.map(report, ReportResponse.class);
        reportResponse.setMemberName(report.getMember().getFullName());
        return reportResponse;
    }
}
