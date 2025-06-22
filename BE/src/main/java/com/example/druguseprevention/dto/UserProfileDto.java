package com.example.druguseprevention.dto;

import com.example.druguseprevention.enums.Gender;
import com.example.druguseprevention.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserProfileDto {
    private Long id;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String address;
    private LocalDate dateOfBirth;
    private Gender gender;
    @Schema(example = "MEMBER")
    private Role role;
}
