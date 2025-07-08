package com.example.druguseprevention.entity;

import com.example.druguseprevention.enums.AppointmentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    LocalDate createAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    AppointmentStatus status;

    @ManyToOne
    @JoinColumn(name = "member_id")
    User member;


    @OneToMany(mappedBy = "appointment")
    List<Report> reports;

    @OneToOne
    @JoinColumn(name = "user_slot_id")
    private UserSlot userSlot;
}
