package com.example.druguseprevention.repository;

import com.example.druguseprevention.entity.ConsultantDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsultantDetailRepository extends JpaRepository<ConsultantDetail, Long> {
    ConsultantDetail findByConsultantId(Long consultantId);
}
