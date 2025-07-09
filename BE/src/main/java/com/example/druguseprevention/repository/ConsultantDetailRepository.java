package com.example.druguseprevention.repository;

import com.example.druguseprevention.entity.ConsultantDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConsultantDetailRepository extends JpaRepository<ConsultantDetail, Long> {
    ConsultantDetail findByConsultantId(Long consultantId);

    List<ConsultantDetail> findByStatus(String status);

}
