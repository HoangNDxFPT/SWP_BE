package com.example.druguseprevention.dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConsultantPublicProfileDto {
    private Long consultantId;
    private String fullName;
    private String degree;
    private String information;
    private String certifiedDegreeImage;
    private String address ;
}
