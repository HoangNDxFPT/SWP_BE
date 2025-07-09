package com.example.druguseprevention.repository;

import com.example.druguseprevention.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
}
