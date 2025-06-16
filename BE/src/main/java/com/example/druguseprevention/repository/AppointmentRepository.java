package com.example.druguseprevention.repository;
import com.example.druguseprevention.entity.Appointment;
import com.example.druguseprevention.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByConsultantId(Long consultantId);
    List<Appointment> findByUserId(Long userId);
    long countByConsultantIdAndStatus(Long consultantId, Appointment.Status status);
}