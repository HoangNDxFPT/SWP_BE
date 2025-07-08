package com.example.druguseprevention.service;

import com.example.druguseprevention.dto.ReportRequest;
import com.example.druguseprevention.entity.Appointment;
import com.example.druguseprevention.entity.Report;
import com.example.druguseprevention.entity.User;
import com.example.druguseprevention.exception.exceptions.BadRequestException;
import com.example.druguseprevention.repository.AppointmentRepository;
import com.example.druguseprevention.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ReportService {
    @Autowired
    ReportRepository reportRepository;
    @Autowired
    AuthenticationService authenticationService;
    @Autowired
    AppointmentRepository appointmentRepository;
    @Autowired
    UserService userService;

    public Report create(ReportRequest reportRequest) {
        User currentMember = userService.getCurrentUser();

        Appointment appointment = appointmentRepository.findById(reportRequest.getAppointmentId())
                .orElseThrow( () -> new BadRequestException("Appointment not found"));

        // check xem appointment nay co phai cua account nay hay ko
        if(appointment.getMember().getId() != currentMember.getId()){
            throw new BadRequestException("This appointment ko phai cua ban");
        }else{
            Report report = new Report();
            report.setAppointment(appointment);
            report.setUser(currentMember);
            report.setReason(reportRequest.getReason());
            report.setCreatedAt(LocalDateTime.now());
            report.setDescription(reportRequest.getDescription());
            return reportRepository.save(report);
        }
    }
}
