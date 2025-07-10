package com.example.druguseprevention.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
public class UserSlot
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "consultant_id")
    private User consultant;

    @ManyToOne
    @JoinColumn(name ="slot_id")
    private Slot slot;

    private boolean isAvailable = true;


    @OneToMany(mappedBy = "userSlot", cascade = CascadeType.ALL)
    private List<Appointment> appointments;
}
