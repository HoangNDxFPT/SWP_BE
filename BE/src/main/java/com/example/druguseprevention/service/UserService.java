package com.example.druguseprevention.service;

import com.example.druguseprevention.dto.ProfileDTO;
import com.example.druguseprevention.entity.User;
import com.example.druguseprevention.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Lấy user hiện tại đang đăng nhập
    public User getCurrentUser() {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUserName(userName)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Lấy profile theo ID
    public ProfileDTO getProfileById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return convertToProfileDTO(user);
    }

    // Cập nhật thông tin profile của người dùng hiện tại
    public void updateProfile(ProfileDTO dto) {
        User user = getCurrentUser();
        user.setFullName(dto.getFullName());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setAddress(dto.getAddress());
        if (dto.getDateOfBirth() != null) {
            user.setDateOfBirth(dto.getDateOfBirth());
        }
        if (dto.getGender() != null) {
            user.setGender(dto.getGender());
        }
        userRepository.save(user);
    }

    // Lấy profile của người dùng hiện tại
    public ProfileDTO getProfile() {
        User user = getCurrentUser();
        return convertToProfileDTO(user);
    }

    // Lấy danh sách profile của các user chưa bị xóa (deleted = false)
    public List<ProfileDTO> getAllProfiles() {
        List<User> users = userRepository.findByDeletedFalse(); // lọc deleted = false
        return users.stream()
                .map(this::convertToProfileDTO)
                .collect(Collectors.toList());
    }

    // Xóa mềm user (chỉ đặt deleted = true)
    public void deleteUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        user.setDeleted(true); // soft delete
        userRepository.save(user);
    }

    // Convert từ Entity -> DTO
    private ProfileDTO convertToProfileDTO(User user) {
        ProfileDTO dto = new ProfileDTO();
        dto.setFullName(user.getFullName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setAddress(user.getAddress());
        dto.setDateOfBirth(user.getDateOfBirth());
        dto.setGender(user.getGender());
        return dto;
    }
}
