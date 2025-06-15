package com.example.druguseprevention.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CertificateDTO {
    private Long courseId;
    private String courseName;
    private LocalDate issueDate;
    private String certificateUrl;
}

