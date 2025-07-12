package com.example.druguseprevention.repository;

import com.example.druguseprevention.entity.Appointment;
import com.example.druguseprevention.entity.User;
import com.example.druguseprevention.entity.UserSlot;
import com.example.druguseprevention.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByMemberAndStatus(User member, AppointmentStatus status);
    Optional<Appointment> findByIdAndMember(Long appointmentId, User member);
    List<Appointment> findAllByUserSlot_ConsultantAndStatus(User consultant, AppointmentStatus status);
    Optional<Appointment> findByIdAndUserSlot_Consultant(Long appointmentId, User consultant);
    Optional<Appointment> findByUserSlotAndStatusIn(UserSlot slot, List<AppointmentStatus> statuses);
    List<Appointment> findByMember(User member);

    @Query("SELECT a FROM Appointment a " +
            "WHERE (:status IS NULL OR a.status = :status) " +
            "AND (:consultant IS NULL OR a.userSlot.consultant = :consultant) " +
            "AND (:member IS NULL OR a.member = :member) " +
            "AND (:date IS NULL OR a.userSlot.date = :date)")
    List<Appointment> searchAppointments(
            @Param("status") AppointmentStatus status,
            @Param("consultant") User consultant,
            @Param("member") User member,
            @Param("date") LocalDate date
    );
}
