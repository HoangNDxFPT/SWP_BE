package com.example.druguseprevention.repository;

import com.example.druguseprevention.entity.ConsultantSchedule;
import com.example.druguseprevention.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ConsultantScheduleRepository extends JpaRepository<ConsultantSchedule, Long> {
    List<ConsultantSchedule> findByConsultant(User consultant);
}
