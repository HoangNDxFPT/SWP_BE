package com.example.druguseprevention.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

@Entity
@Getter
@Setter
public class Slot
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String label;
    private LocalTime start;
    private LocalTime end;
    private boolean isDelete =false;

    @OneToMany(mappedBy = "slot")
    @JsonIgnore
    private List<UserSlot> userSlots;
}
