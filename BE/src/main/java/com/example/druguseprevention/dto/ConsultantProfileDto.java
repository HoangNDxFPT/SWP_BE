package com.example.druguseprevention.dto;

import lombok.Data;

@Data
public class ConsultantProfileDto {
    private Long consultantId;
    private String fullName;
    private String phoneNumber;
    private String address;
    private String degree;
    private String information;
    private String certifiedDegree;
    private String certifiedDegreeImage;
    private String googleMeetLink;
}
