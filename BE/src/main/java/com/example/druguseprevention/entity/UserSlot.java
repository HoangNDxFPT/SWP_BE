package com.example.druguseprevention.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

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
    @JoinColumn(name = "ussr_id")
    private User user;

    @ManyToOne
    @JoinColumn(name ="slot_id")
    private Slot slot;

    private boolean isAvailable = true;

}
