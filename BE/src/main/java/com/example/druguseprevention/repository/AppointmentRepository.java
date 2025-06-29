package com.example.druguseprevention.repository;
import com.example.druguseprevention.entity.Appointment;
import com.example.druguseprevention.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByConsultantId(Long consultantId);
    List<Appointment> findByUserId(Long userId);
    long countByConsultantIdAndStatus(Long consultantId, Appointment.Status status);

    @Query("SELECT a FROM Appointment a WHERE a.consultant.id = :consultantId AND DATE(a.appointmentTime) = :date")
    List<Appointment> findByConsultantIdAndDate(@Param("consultantId") Long consultantId, @Param("date") LocalDate date);
}