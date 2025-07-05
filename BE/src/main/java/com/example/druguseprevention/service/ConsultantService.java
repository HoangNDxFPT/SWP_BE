package com.example.druguseprevention.service;

import com.example.druguseprevention.dto.ConsultantProfileDto;
import com.example.druguseprevention.dto.ConsultantPublicProfileDto;
import com.example.druguseprevention.dto.UserProfileDto;

import java.util.List;

public interface ConsultantService {
    void updateProfile(Long consultantId, ConsultantProfileDto profile);

    ConsultantProfileDto getProfile(Long consultantId);

    ConsultantPublicProfileDto getPublicConsultantProfile(Long consultantId);

    Long getUserIdByUsername(String username);

    List<UserProfileDto> getAllMemberProfiles();
}
