package com.example.druguseprevention.entity;



import com.example.druguseprevention.enums.AssessmentType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Assessment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private AssessmentType type;

    @ManyToOne @JoinColumn(name="member_id", nullable=false)
    private User member;

    private LocalDateTime createdAt;

    private boolean submitted = false;

    // Thay đổi từ single substance thành multiple substances
    @ManyToMany
    @JoinTable(
        name = "assessment_substances",
        joinColumns = @JoinColumn(name = "assessment_id"),
        inverseJoinColumns = @JoinColumn(name = "substance_id")
    )
    private List<Substance> substances = new ArrayList<>();

    @OneToMany(mappedBy = "assessment", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<AssessmentResult> results;

}
