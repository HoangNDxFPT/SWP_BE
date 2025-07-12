package com.example.druguseprevention.repository;

import com.example.druguseprevention.entity.Report;
import com.example.druguseprevention.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByMember (User member);
}
