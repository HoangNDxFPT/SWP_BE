package com.example.druguseprevention.dto;

import com.example.druguseprevention.enums.Gender;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

@Data
public class RegisterRequest {
    private String userName;
    private String password;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String address;
    private LocalDate dateOfBirth;
    private Gender gender;
}


