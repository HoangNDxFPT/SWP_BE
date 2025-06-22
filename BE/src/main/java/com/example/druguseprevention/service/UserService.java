package com.example.druguseprevention.service;

import com.example.druguseprevention.dto.AdminProfileDTO;
import com.example.druguseprevention.dto.ChangePasswordRequest;
import com.example.druguseprevention.dto.ProfileDTO;
import com.example.druguseprevention.entity.User;
import com.example.druguseprevention.enums.Role;
import com.example.druguseprevention.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Kiểm tra xem user hiện tại có phải là admin hay không
    public boolean isCurrentUserAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    // Lấy user hiện tại đang đăng nhập
    public User getCurrentUser() {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUserName(userName)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Lấy danh sách profile của các user chưa bị xóa (deleted = false)
    public List<AdminProfileDTO> getAllProfiles() {
        // Chỉ admin mới được xem danh sách user
        if (!isCurrentUserAdmin()) {
            throw new RuntimeException("Only administrators can view all user profiles");
        }

        List<User> users = userRepository.findByDeletedFalse();
        return users.stream()
                .map(this::convertToAdminProfileDTO)
                .collect(Collectors.toList());
    }

    // Lấy profile theo ID - trả về thông tin phù hợp với role
    public AdminProfileDTO getProfileById(Long id) {
        // Chỉ admin mới được xem thông tin user khác
        if (!isCurrentUserAdmin()) {
            throw new RuntimeException("Only administrators can view other user profiles");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return convertToAdminProfileDTO(user);
    }

    // Lấy profile của người dùng hiện tại
    public ProfileDTO getProfile() {
        User user = getCurrentUser();

        if (isCurrentUserAdmin()) {
            return convertToAdminProfileDTO(user);
        }

        return convertToProfileDTO(user);
    }

    // Cập nhật thông tin profile của người dùng hiện tại
    public void updateProfile(ProfileDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + dto.getUserId()));

        // Nếu không phải admin và không phải chính mình
        if (!isCurrentUserAdmin() && !user.getId().equals(getCurrentUser().getId())) {
            throw new RuntimeException("You can only update your own profile");
        }

        // Cập nhật thông tin cơ bản
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

    // Admin cập nhật profile người dùng (bao gồm role)
    public void adminUpdateProfile(AdminProfileDTO dto) {
        if (!isCurrentUserAdmin()) {
            throw new RuntimeException("Only administrators can update user profiles with role");
        }

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + dto.getUserId()));

        // Cập nhật thông tin cơ bản
        user.setFullName(dto.getFullName());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setAddress(dto.getAddress());
        if (dto.getDateOfBirth() != null) {
            user.setDateOfBirth(dto.getDateOfBirth());
        }
        if (dto.getGender() != null) {
            user.setGender(dto.getGender());
        }

        // Cập nhật email nếu có thay đổi
        if (dto.getEmail() != null && !dto.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(dto.getEmail())) {
                throw new RuntimeException("Email already exists");
            }
            user.setEmail(dto.getEmail());
        }

        // Cập nhật role nếu có
        if (dto.getRole() != null) {
            // Không cho phép thay đổi role của admin khác
            if (user.getRole() == Role.ADMIN && !user.getId().equals(getCurrentUser().getId())) {
                throw new RuntimeException("Cannot change role of another admin user");
            }
            user.setRole(dto.getRole());
        }

        userRepository.save(user);
    }

    // Cập nhật mật khẩu (hỗ trợ cả admin và người dùng tự đổi)
    public void updatePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        User currentUser = getCurrentUser();
        boolean isAdmin = isCurrentUserAdmin();

        // Trường hợp 1: Admin thay đổi mật khẩu cho người dùng khác
        if (isAdmin && !userId.equals(currentUser.getId())) {
            // Admin không cần mật khẩu cũ, chỉ cần mật khẩu mới
            if (request.getNewPassword() == null || request.getNewPassword().isEmpty()) {
                throw new RuntimeException("New password is required");
            }

            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }
        // Trường hợp 2: Người dùng thay đổi mật khẩu của chính họ
        else {
            // Người dùng cần cung cấp mật khẩu cũ và mới
            if (request.getOldPassword() == null || request.getNewPassword() == null) {
                throw new RuntimeException("Both old and new passwords are required");
            }

            // Xác thực mật khẩu cũ
            if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
                throw new RuntimeException("Current password is incorrect");
            }

            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }

        userRepository.save(user);
    }

    // Xóa mềm user (chỉ đặt deleted = true)
    public void deleteUserById(Long id) {
        // Chỉ admin mới được xóa user
        if (!isCurrentUserAdmin()) {
            throw new RuntimeException("Only administrators can delete users");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Ngăn chặn việc xóa chính mình
        if (user.getId().equals(getCurrentUser().getId())) {
            throw new RuntimeException("Cannot delete your own account");
        }

        user.setDeleted(true); // soft delete
        userRepository.save(user);
    }

    // Convert từ Entity -> DTO cơ bản
    private ProfileDTO convertToProfileDTO(User user) {
        ProfileDTO dto = new ProfileDTO();
        dto.setUserId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setAddress(user.getAddress());
        dto.setDateOfBirth(user.getDateOfBirth());
        dto.setGender(user.getGender());
        return dto;
    }

    // Convert từ Entity -> AdminDTO đầy đủ
    private AdminProfileDTO convertToAdminProfileDTO(User user) {
        AdminProfileDTO dto = new AdminProfileDTO();
        // Copy thông tin cơ bản
        dto.setUserId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setAddress(user.getAddress());
        dto.setDateOfBirth(user.getDateOfBirth());
        dto.setGender(user.getGender());

        // Thêm thông tin chỉ admin mới được xem
        dto.setUserName(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setDeleted(user.isDeleted());

        return dto;
    }
}