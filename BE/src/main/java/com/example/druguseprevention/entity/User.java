package com.example.druguseprevention.entity;

import com.example.druguseprevention.enums.Gender;
import com.example.druguseprevention.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

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

    @Enumerated(EnumType.STRING)
    Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.role));
    }

    @Override
    public String getUsername() {
        return this.userName;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Column(nullable = false)
    private boolean deleted = false;


    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Assessment> assessments;

    @OneToMany(mappedBy = "member")
    @JsonIgnore
    private List<ProgramParticipation> programParticipations;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<SurveySendHistory> surveySendHistories;

    @OneToMany(mappedBy = "consultant")
    @JsonIgnore
    private List<UserSlot> userSlots;

    @OneToMany(mappedBy = "member")
    @JsonIgnore
    private List<Appointment> appointments;

    @OneToMany(mappedBy = "member")
    @JsonIgnore
    private List<Report> reports;
}
