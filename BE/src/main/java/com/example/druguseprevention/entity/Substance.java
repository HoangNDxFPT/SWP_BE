package com.example.druguseprevention.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Substance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // Tên chất: Tobacco, Alcohol, Cannabis, etc.
    private String description; // Mô tả chi tiết
    private boolean isDeleted = false;

    @OneToMany(mappedBy = "substance")
    @JsonIgnore
    private List<AssessmentQuestion> questions;
}
