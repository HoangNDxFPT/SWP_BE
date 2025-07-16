package com.example.druguseprevention.dto;

import com.example.druguseprevention.enums.Gender;
import com.example.druguseprevention.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

@Data
public class RegisterRequest {
    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters")
    @Column(nullable = false, unique = true)
    String userName;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Column(nullable = false)
    String password;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(nullable = false, unique = true)
    String email;

    @NotBlank(message = "Full name is required")
    @Size(max = 100)
    String fullName;

    @Pattern(regexp = "^(0[3|5|7|8|9])[0-9]{8}$", message = "Phone number not valid!")
    String phoneNumber;

    @Size(max = 100, message = "Address must be less than 100 characters")
    String address;

    @Past(message = "Date of birth must be in the past")
    LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    Gender gender;

}


