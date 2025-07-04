//package com.example.druguseprevention.service;
//
//import com.example.druguseprevention.dto.ConsultantScheduleRequest;
//import com.example.druguseprevention.dto.ConsultantScheduleResponse;
//import com.example.druguseprevention.entity.ConsultantSchedule;
//import com.example.druguseprevention.entity.User;
//import com.example.druguseprevention.repository.AppointmentRepository;
//import com.example.druguseprevention.repository.ConsultantScheduleRepository;
//import com.example.druguseprevention.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class ConsultantScheduleServiceImpl implements ConsultantScheduleService {
//
//    private final ConsultantScheduleRepository scheduleRepo;
//    private final UserRepository userRepo;
//    private final AppointmentRepository appointmentRepo;
//
//    private ConsultantScheduleResponse mapToDto(ConsultantSchedule schedule) {
//        int bookedCount = appointmentRepo.countByConsultantIdAndTimeRange(
//                schedule.getConsultant().getId(),
//                schedule.getWorkDate(),
//                schedule.getStartTime(),
//                schedule.getEndTime()
//        );
//
//        boolean isAvailable = bookedCount < schedule.getMaxAppointments();
//
//        return ConsultantScheduleResponse.builder()
//                .scheduleId(schedule.getScheduleId())
//                .consultantId(schedule.getConsultant().getId())
//                .consultantName(schedule.getConsultant().getFullName())
//                .workDate(schedule.getWorkDate())
//                .startTime(schedule.getStartTime())
//                .endTime(schedule.getEndTime())
//                .maxAppointments(schedule.getMaxAppointments())
//                .available(isAvailable)
//                .build();
//    }
//
//    @Override
//    public ConsultantScheduleResponse create(ConsultantScheduleRequest request) {
//        User consultant = userRepo.findById(request.getConsultantId())
//                .orElseThrow(() -> new RuntimeException("Consultant not found"));
//
//        ConsultantSchedule schedule = ConsultantSchedule.builder()
//                .consultant(consultant)
//                .workDate(request.getWorkDate())
//                .startTime(request.getStartTime())
//                .endTime(request.getEndTime())
//                .maxAppointments(request.getMaxAppointments() != null ? request.getMaxAppointments() : 1)
//                .build();
//
//        return mapToDto(scheduleRepo.save(schedule));
//    }
//
//    @Override
//    public ConsultantScheduleResponse update(Long id, ConsultantScheduleRequest request) {
//        ConsultantSchedule schedule = scheduleRepo.findById(id)
//                .orElseThrow(() -> new RuntimeException("Schedule not found"));
//
//        User consultant = userRepo.findById(request.getConsultantId())
//                .orElseThrow(() -> new RuntimeException("Consultant not found"));
//
//        schedule.setConsultant(consultant);
//        schedule.setWorkDate(request.getWorkDate());
//        schedule.setStartTime(request.getStartTime());
//        schedule.setEndTime(request.getEndTime());
//        schedule.setMaxAppointments(request.getMaxAppointments());
//
//        return mapToDto(scheduleRepo.save(schedule));
//    }
//
//    @Override
//    public void delete(Long id) {
//        scheduleRepo.deleteById(id);
//    }
//
//    @Override
//    public List<ConsultantScheduleResponse> getAll() {
//        return scheduleRepo.findAll().stream()
//                .map(this::mapToDto)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public List<ConsultantScheduleResponse> getByConsultantId(Long consultantId) {
//        User consultant = userRepo.findById(consultantId)
//                .orElseThrow(() -> new RuntimeException("Consultant not found"));
//
//        return scheduleRepo.findByConsultant(consultant).stream()
//                .map(this::mapToDto)
//                .collect(Collectors.toList());
//    }
//}
