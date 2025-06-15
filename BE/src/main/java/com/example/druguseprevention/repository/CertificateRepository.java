package com.example.druguseprevention.repository;

import com.example.druguseprevention.entity.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    Optional<Certificate> findByUserIdAndCourseId(Long userId, Long courseId);
    List<Certificate> findAllByUserId(Long userId);
}
