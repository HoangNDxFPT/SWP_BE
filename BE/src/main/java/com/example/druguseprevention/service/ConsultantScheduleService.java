package com.example.druguseprevention.service;

import com.example.druguseprevention.dto.ConsultantScheduleRequest;
import com.example.druguseprevention.dto.ConsultantScheduleResponse;

import java.util.List;

public interface ConsultantScheduleService {
    ConsultantScheduleResponse create(ConsultantScheduleRequest request);
    ConsultantScheduleResponse update(Long id, ConsultantScheduleRequest request);
    void delete(Long id);
    List<ConsultantScheduleResponse> getAll();
    List<ConsultantScheduleResponse> getByConsultantId(Long consultantId);
}
