package com.example.druguseprevention.entity;

import com.example.druguseprevention.enums.Gender;
import com.example.druguseprevention.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

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


    String userName;


    String password;


    String email;


    String fullName;


    String phoneNumber;


    String address;


    LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    Gender gender;

    @Enumerated(EnumType.STRING)
    Role role;

    @Override
    public boolean isEnabled() {
        return !deleted && isActive; // Phải active VÀ chưa bị delete
    }

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

    @Column(nullable = false)
    private boolean isActive = false;

    @Column(length = 500)
    private String activationToken;

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
