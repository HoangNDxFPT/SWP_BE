package com.example.druguseprevention.dto;

import com.example.druguseprevention.enums.Gender;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ProfileDTO {
    private Long userId;
    private String fullName;
    private String phoneNumber;
    private String address;
    private LocalDate dateOfBirth;
    private Gender gender;
//    private Role role;
}
