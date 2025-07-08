package com.example.druguseprevention.repository;


import com.example.druguseprevention.entity.User;
import com.example.druguseprevention.entity.UserSlot;
import com.example.druguseprevention.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface UserSlotRepository extends JpaRepository<UserSlot, Long> {
    List<UserSlot> findUserSlotsByConsultantAndDate (User consultant, LocalDate date);

    UserSlot findUserSlotBySlotIdAndConsultantAndDate(long slotId, User consultant, LocalDate date);
}
