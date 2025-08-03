package com.example.druguseprevention.service;

import com.example.druguseprevention.dto.ConsultantFullProfileDto;
import com.example.druguseprevention.dto.ConsultantProfileDto;
import com.example.druguseprevention.dto.ConsultantPublicProfileDto;
import com.example.druguseprevention.dto.UserProfileDto;
import com.example.druguseprevention.entity.ConsultantDetail;

import java.util.List;

public interface ConsultantService {
    void updateProfile(Long consultantId, ConsultantProfileDto profile);
    ConsultantProfileDto getProfile(Long consultantId);
    ConsultantPublicProfileDto getPublicConsultantProfile(Long consultantId);
    Long getUserIdByUsername(String username);
    List<UserProfileDto> getAllMemberProfiles();
    void saveConsultantDetail(ConsultantDetail detail);
    ConsultantDetail getConsultantDetailById(Long consultantId);
    List<ConsultantPublicProfileDto> getAllConsultants();
    List<ConsultantPublicProfileDto> getAllPublicConsultants();
    List<ConsultantFullProfileDto> getAllConsultantFullProfiles();
}
