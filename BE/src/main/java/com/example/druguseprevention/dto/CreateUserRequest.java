package com.example.druguseprevention.dto;

import com.example.druguseprevention.enums.Gender;
import com.example.druguseprevention.enums.Role;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateUserRequest {
    private String userName;
    private String password;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String address;
    private LocalDate dateOfBirth;
    private Gender gender;

    // ✅ Chỉ field mới cho admin: chọn vai trò người dùng
    private Role role;
}
