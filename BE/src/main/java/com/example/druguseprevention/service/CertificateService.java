package com.example.druguseprevention.service;

import com.example.druguseprevention.dto.CertificateDTO;

import java.util.List;

public interface CertificateService {
    List<CertificateDTO> getUserCertificates(Long userId);
    CertificateDTO generateCertificate(Long userId, Long courseId);
}
