//package com.example.druguseprevention.repository;
//import com.example.druguseprevention.entity.Appointment;
//import com.example.druguseprevention.entity.User;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//
//import java.time.LocalDate;
//import java.time.LocalTime;
//import java.util.List;
//
//public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
//    List<Appointment> findByConsultantId(Long consultantId);
//    List<Appointment> findByUserId(Long userId);
//    long countByConsultantIdAndStatus(Long consultantId, Appointment.Status status);
//
//    @Query("SELECT a FROM Appointment a WHERE a.consultant.id = :consultantId AND DATE(a.appointmentTime) = :date")
//    List<Appointment> findByConsultantIdAndDate(@Param("consultantId") Long consultantId, @Param("date") LocalDate date);
//    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.consultant.id = :consultantId AND DATE(a.appointmentTime) = :date AND TIME(a.appointmentTime) >= :start AND TIME(a.appointmentTime) < :end")
//    int countByConsultantIdAndTimeRange(@Param("consultantId") Long consultantId,
//                                        @Param("date") LocalDate date,
//                                        @Param("start") LocalTime start,
//                                        @Param("end") LocalTime end);
//
//}