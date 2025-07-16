package com.example.druguseprevention.service;

import com.example.druguseprevention.dto.ConsultantFullProfileDto;
import com.example.druguseprevention.dto.ConsultantProfileDto;
import com.example.druguseprevention.dto.ConsultantPublicProfileDto;
import com.example.druguseprevention.dto.UserProfileDto;
import com.example.druguseprevention.entity.ConsultantDetail;
import com.example.druguseprevention.entity.User;
import com.example.druguseprevention.enums.Role;
import com.example.druguseprevention.repository.ConsultantDetailRepository;
import com.example.druguseprevention.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConsultantServiceImpl implements ConsultantService {

    private final UserRepository userRepository;
    private final ConsultantDetailRepository consultantDetailRepository;

    @Override
    public void updateProfile(Long consultantId, ConsultantProfileDto dto) {
        User consultant = userRepository.findById(consultantId)
                .orElseThrow(() -> new RuntimeException("Consultant not found"));

        consultant.setFullName(dto.getFullName());
        consultant.setPhoneNumber(dto.getPhoneNumber());
        consultant.setAddress(dto.getAddress());
        userRepository.save(consultant);

        ConsultantDetail detail = consultantDetailRepository.findByConsultantId(consultantId);
        if (detail == null) {
            detail = new ConsultantDetail();
            detail.setConsultant(consultant);
        }

        detail.setDegree(dto.getDegree());
        detail.setInformation(dto.getInformation());
        detail.setCertifiedDegree(dto.getCertifiedDegree());
        detail.setCertifiedDegreeImage(dto.getCertifiedDegreeImage());
        detail.setGoogleMeetLink(dto.getGoogleMeetLink());
        detail.setAvatarUrl(dto.getAvatarUrl()); // ✅ Lưu avatar URL từ FE

        consultantDetailRepository.save(detail);
    }

    @Override
    public ConsultantProfileDto getProfile(Long consultantId) {
        User user = (User) userRepository.findByIdAndDeletedFalse(consultantId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tư vấn viên"));

        ConsultantDetail detail = consultantDetailRepository.findByConsultantId(consultantId);

        ConsultantProfileDto dto = new ConsultantProfileDto();
        dto.setConsultantId(consultantId);
        dto.setFullName(user.getFullName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setAddress(user.getAddress());

        if (detail != null) {
            dto.setDegree(detail.getDegree());
            dto.setInformation(detail.getInformation());
            dto.setCertifiedDegree(detail.getCertifiedDegree());
            dto.setCertifiedDegreeImage(detail.getCertifiedDegreeImage());
            dto.setGoogleMeetLink(detail.getGoogleMeetLink());
            dto.setAvatarUrl(detail.getAvatarUrl()); // Trả avatar URL cho FE nếu cần
        }

        return dto;
    }

    @Override
    public ConsultantPublicProfileDto getPublicConsultantProfile(Long consultantId) {
        User user = (User) userRepository.findByIdAndDeletedFalse(consultantId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tư vấn viên"));

        if (user.getRole() != Role.CONSULTANT) {
            throw new RuntimeException("Người dùng không phải là tư vấn viên");
        }

        ConsultantDetail detail = consultantDetailRepository.findByConsultantId(consultantId);

        ConsultantPublicProfileDto dto = new ConsultantPublicProfileDto();
        dto.setConsultantId(consultantId);
        dto.setFullName(user.getFullName());
        dto.setAddress(user.getAddress());

        if (detail != null) {
            dto.setDegree(detail.getDegree());
            dto.setInformation(detail.getInformation());
            dto.setCertifiedDegreeImage(detail.getCertifiedDegreeImage());
            dto.setAvatarUrl(detail.getAvatarUrl()); // Trả avatar URL
        }

        return dto;
    }

    @Override
    public Long getUserIdByUsername(String username) {
        return userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
    }

    @Override
    public List<UserProfileDto> getAllMemberProfiles() {
        return userRepository.findByRoleAndDeletedFalse(Role.MEMBER)
                .stream()
                .map(user -> {
                    UserProfileDto dto = new UserProfileDto();
                    dto.setId(user.getId());
                    dto.setEmail(user.getEmail());
                    dto.setFullName(user.getFullName());
                    dto.setPhoneNumber(user.getPhoneNumber());
                    dto.setAddress(user.getAddress());
                    dto.setDateOfBirth(user.getDateOfBirth());
                    dto.setGender(user.getGender());
                    dto.setRole(user.getRole());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public ConsultantDetail getConsultantDetailById(Long consultantId) {
        return consultantDetailRepository.findById(consultantId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ConsultantDetail với ID: " + consultantId));
    }

    @Override
    public void saveConsultantDetail(ConsultantDetail detail) {
        consultantDetailRepository.save(detail);
    }

    @Override
    public List<ConsultantPublicProfileDto> getAllConsultants() {
        return getAllConsultantProfiles();
    }

    @Override
    public List<ConsultantPublicProfileDto> getAllPublicConsultants() {
        return getAllConsultantProfiles();
    }

    private List<ConsultantPublicProfileDto> getAllConsultantProfiles() {
        List<User> consultants = userRepository.findByRoleAndDeletedFalse(Role.CONSULTANT);

        return consultants.stream()
                .map(user -> {
                    ConsultantDetail detail = consultantDetailRepository.findByConsultantId(user.getId());

                    ConsultantPublicProfileDto dto = new ConsultantPublicProfileDto();
                    dto.setConsultantId(user.getId());
                    dto.setFullName(user.getFullName());
                    dto.setAddress(user.getAddress());

                    if (detail != null) {
                        dto.setDegree(detail.getDegree());
                        dto.setInformation(detail.getInformation());
                        dto.setCertifiedDegreeImage(detail.getCertifiedDegreeImage());
                        dto.setAvatarUrl(detail.getAvatarUrl()); //  Trả avatar URL
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }
    @Override
    public List<ConsultantFullProfileDto> getAllConsultantFullProfiles() {
        return userRepository.findByRole(Role.CONSULTANT).stream()
                .map((User user) -> {
                    ConsultantDetail detail = consultantDetailRepository.findByConsultantId(user.getId());

                    ConsultantFullProfileDto dto = new ConsultantFullProfileDto();
                    dto.setConsultantId(user.getId());
                    dto.setFullName(user.getFullName());
                    dto.setPhoneNumber(user.getPhoneNumber());
                    dto.setAddress(user.getAddress());

                    if (detail != null) {
                        dto.setAvatarUrl(detail.getAvatarUrl());
                        dto.setDegree(detail.getDegree());
                        dto.setInformation(detail.getInformation());
                        dto.setCertifiedDegree(detail.getCertifiedDegree());
                        dto.setCertifiedDegreeImage(detail.getCertifiedDegreeImage());
                        dto.setGoogleMeetLink(detail.getGoogleMeetLink());
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }

}

