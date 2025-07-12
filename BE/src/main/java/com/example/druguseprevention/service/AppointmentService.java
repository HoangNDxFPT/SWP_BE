package com.example.druguseprevention.service;

import com.example.druguseprevention.dto.*;
import com.example.druguseprevention.entity.*;
import com.example.druguseprevention.enums.AppointmentStatus;
import com.example.druguseprevention.enums.Role;
import com.example.druguseprevention.exception.exceptions.BadRequestException;
import com.example.druguseprevention.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AppointmentService {
    @Autowired
    AppointmentRepository appointmentRepository;

    @Autowired
    UserSlotRepository userSlotRepository;

    @Autowired
    AuthenticationRepository authenticationRepository;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    UserService userService;

    @Autowired
    ConsultantDetailRepository consultantDetailRepository;

    @Transactional
    public AppointmentResponse create(AppointmentRequest appointmentRequest) {
        // 1. Lấy consultant
        User consultant = authenticationRepository.findById(appointmentRequest.getConsultantId())
                .orElseThrow(() -> new BadRequestException("Consultant not found"));

        if (!consultant.getRole().equals(Role.CONSULTANT)) {
            throw new BadRequestException("Account is not a consultant");
        }

        // 2. Lấy slot
        UserSlot slot = userSlotRepository.findUserSlotBySlotIdAndConsultantAndDate(
                appointmentRequest.getSlotId(),
                consultant,
                appointmentRequest.getAppointmentDate()
        );

        if (!slot.isAvailable()) {
            throw new BadRequestException("Slot is not available");
        }

        // 3. Lấy người dùng hiện tại
        User currentMember = userService.getCurrentUser();

        // 4. Tạo Appointment
        Appointment appointment = new Appointment();
        appointment.setCreateAt(LocalDate.now());
        appointment.setStatus(AppointmentStatus.PENDING);
        appointment.setMember(currentMember);
        appointment.setUserSlot(slot);
        appointmentRepository.save(appointment);

        // 5. Đánh dấu slot đã được đặt
        slot.setAvailable(false);


        // 6. Lấy link Google Meet từ ConsultantDetail
        ConsultantDetail detail = consultantDetailRepository.findByConsultant(consultant)
                .orElseThrow(() -> new BadRequestException("Consultant detail not found"));

        // 7. Trả về response có thông tin meet link
        Slot slotInfo = slot.getSlot();
        return new AppointmentResponse(
                appointment.getId(),
                appointment.getCreateAt(),
                appointment.getStatus(),
                consultant.getFullName(),
                slot.getDate(),
                slotInfo.getStart().toString(),
                slotInfo.getEnd().toString(),
                detail.getGoogleMeetLink()
        );
    }


    @Transactional
    public AppointmentResponseForConsultant createByConsultant(AppointmentRequestForConsultant request) {
        // 1. Lấy user cần đặt lịch
        User member = authenticationRepository.findById(request.getUserId())
                .orElseThrow(() -> new BadRequestException("User not found"));

        if (!member.getRole().equals(Role.MEMBER)) {
            throw new BadRequestException("Appointment must be for a normal member");
        }

        // 2. Lấy consultant hiện tại
        User consultant = userService.getCurrentUser();

        if (!consultant.getRole().equals(Role.CONSULTANT)) {
            throw new BadRequestException("Only consultants can use this function");
        }

        // 3. Kiểm tra slot của consultant còn trống không
        UserSlot slot = userSlotRepository.findUserSlotBySlotIdAndConsultantAndDate(
                request.getSlotId(), consultant, request.getAppointmentDate());

        if (slot == null || !slot.isAvailable()) {
            throw new BadRequestException("Slot not available");
        }

        // 4. Tạo cuộc hẹn
        Appointment appointment = new Appointment();
        appointment.setCreateAt(LocalDate.now());
        appointment.setStatus(AppointmentStatus.PENDING);
        appointment.setMember(member); // Gán member là người được hẹn
        appointment.setUserSlot(slot);
        appointmentRepository.save(appointment);

        // 5. Đánh dấu slot là đã đặt
        slot.setAvailable(false);

        // 6. Lấy meet link
        ConsultantDetail detail = consultantDetailRepository.findByConsultant(consultant)
                .orElseThrow(() -> new BadRequestException("Consultant detail not found"));

        // 7. Trả về response
        Slot slotInfo = slot.getSlot();
        return new AppointmentResponseForConsultant(
                appointment.getId(),
                appointment.getCreateAt(),
                appointment.getStatus(),
                member.getFullName(),
                member.getPhoneNumber(),
                member.getEmail(),
                slot.getDate(),
                slotInfo.getStart().toString(),
                slotInfo.getEnd().toString(),
                detail.getGoogleMeetLink()
        );
    }

    public List<AppointmentResponse> getMyAppointmentsByStatus(AppointmentStatus status) {
        User currentUser = userService.getCurrentUser();
        List<Appointment> appointments = appointmentRepository.findByMemberAndStatus(currentUser, status);

        return appointments.stream()
                .map(appointment -> {
                    UserSlot slot = appointment.getUserSlot();
                    Slot timeSlot = slot.getSlot();
                    User consultant = slot.getConsultant();

                    ConsultantDetail detail = consultantDetailRepository.findByConsultant(consultant)
                            .orElseThrow(() -> new BadRequestException("Consultant detail not found"));


                    return new AppointmentResponse(
                            appointment.getId(),
                            appointment.getCreateAt(),
                            appointment.getStatus(),
                            consultant.getFullName(),
                            slot.getDate(),
                            timeSlot.getStart().toString(),
                            timeSlot.getEnd().toString(),
                            detail.getGoogleMeetLink()
                    );
                })
                .collect(Collectors.toList());
    }

    public List<AppointmentResponseForConsultant> getAppointmentsForConsultantByStatus(AppointmentStatus status) {
        User consultant = userService.getCurrentUser();

        // Truy vấn trực tiếp các cuộc hẹn của consultant theo status
        List<Appointment> appointments = appointmentRepository.findAllByUserSlot_ConsultantAndStatus(consultant, status);

        return appointments.stream()
                .map(appointment -> {
                    UserSlot slot = appointment.getUserSlot();
                    Slot timeSlot = slot.getSlot();
                    User member = appointment.getMember();

                    ConsultantDetail detail = consultantDetailRepository.findByConsultant(consultant)
                            .orElseThrow(() -> new BadRequestException("Consultant detail not found"));

                    return new AppointmentResponseForConsultant(
                            appointment.getId(),
                            appointment.getCreateAt(),
                            appointment.getStatus(),
                            member.getFullName(),
                            member.getPhoneNumber(),
                            member.getEmail(),
                            slot.getDate(),
                            timeSlot.getStart().toString(),
                            timeSlot.getEnd().toString(),
                            detail.getGoogleMeetLink()
                    );
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void cancelAppointment(Long appointmentId) {
        User currentUser = userService.getCurrentUser();

        Appointment appointment = appointmentRepository.findByIdAndMember(appointmentId, currentUser)
                .orElseThrow(() -> new BadRequestException("Appointment not found or not owned by user"));

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new BadRequestException("Appointment already cancelled");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);

        UserSlot slot = appointment.getUserSlot();
        if (slot != null) {
//            slot.setAvailable(true);
//            slot.setAppointment(null);
//            userSlotRepository.save(slot);
            UserSlot managedSlot = userSlotRepository.findById(slot.getId())
                    .orElseThrow(() -> new BadRequestException("UserSlot not found"));

            managedSlot.setAvailable(true);
            userSlotRepository.save(managedSlot);
        }
    }

    @Transactional
    public void updateAppointmentStatus(UpdateAppointmentStatusRequest request) {
        User consultant = userService.getCurrentUser();


        Appointment appointment = appointmentRepository
                .findByIdAndUserSlot_Consultant(request.getAppointmentId(), consultant)
                .orElseThrow(() -> new BadRequestException("Appointment not found or not assigned to you."));

        //  Không cho cập nhật nếu đã bị cancel hoặc completed
        if (appointment.getStatus() == AppointmentStatus.CANCELLED ||
                appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new BadRequestException("Cannot update status of cancelled or completed appointment.");
        }

        appointment.setStatus(request.getStatus());

        appointmentRepository.save(appointment);
    }

    // Admin xem tất cả lịch hẹn (có thể lọc theo ngày, trạng thái, consultant, member)
    public List<AppointmentAdminResponse> searchAppointments(AppointmentSearchRequest request) {
        User consultant = null;
        if (request.getConsultantId() != null) {
            consultant = authenticationRepository.findById(request.getConsultantId())
                    .orElseThrow(() -> new BadRequestException("Consultant not found"));
        }

        User member = null;
        if (request.getMemberId() != null) {
            member = authenticationRepository.findById(request.getMemberId())
                    .orElseThrow(() -> new BadRequestException("Member not found"));
        }

        List<Appointment> appointments = appointmentRepository.searchAppointments(
                request.getStatus(), consultant, member, request.getDate()
        );

        return appointments.stream()
                .map(this::toAdminResponse)
                .collect(Collectors.toList());
    }

    private AppointmentAdminResponse toAdminResponse(Appointment appointment) {
        UserSlot slot = appointment.getUserSlot();
        Slot slotInfo = slot.getSlot();
        return new AppointmentAdminResponse(
                appointment.getId(),
                appointment.getCreateAt(),
                appointment.getStatus(),
                appointment.getMember().getFullName(),
                slot.getConsultant().getFullName(),
                slot.getDate(),
                slotInfo.getStart().toString(),
                slotInfo.getEnd().toString()
        );
    }

    //Admin xem lịch sử đặt lịch của một member bất kỳ
//    public List<AppointmentAdminResponse> getAppointmentsOfMember(Long memberId) {
//
//
//        User member = authenticationRepository.findById(memberId)
//                .orElseThrow(() -> new BadRequestException("Member not found"));
//
//        List<Appointment> appointments = appointmentRepository.findByMember(member);
//
//        return appointments.stream()
//                .map(this::toAdminResponse)
//                .collect(Collectors.toList());
//    }

}
