package com.example.druguseprevention.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "consultant_detail")
@Data
public class ConsultantDetail {

    @Id
    private Long consultantId;

    @Column(length = 20)
    private String status;

    @Column(columnDefinition = "TEXT")
    private String degree;

    @Column(columnDefinition = "TEXT")
    private String information;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "consultant_id")
    private User user;
    @Column(name = "certified_degree")
    private String certifiedDegree;

}
