package com.example.druguseprevention.repository;

import com.example.druguseprevention.entity.Appointment;
import com.example.druguseprevention.entity.User;
import com.example.druguseprevention.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByMemberAndStatus(User member, AppointmentStatus status);
    Optional<Appointment> findByIdAndMember(Long appointmentId, User member);
    List<Appointment> findAllByUserSlot_ConsultantAndStatus(User consultant, AppointmentStatus status);
    Optional<Appointment> findByIdAndUserSlot_Consultant(Long appointmentId, User consultant);
}
