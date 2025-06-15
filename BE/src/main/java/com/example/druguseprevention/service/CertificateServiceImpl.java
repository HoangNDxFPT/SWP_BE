package com.example.druguseprevention.service;

import com.example.druguseprevention.dto.CertificateDTO;
import com.example.druguseprevention.entity.Certificate;
import com.example.druguseprevention.entity.Course;
import com.example.druguseprevention.entity.User;
import com.example.druguseprevention.repository.CertificateRepository;
import com.example.druguseprevention.repository.CourseRepository;
import com.example.druguseprevention.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CertificateServiceImpl implements CertificateService {

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Override
    public List<CertificateDTO> getUserCertificates(Long userId) {
        return certificateRepository.findAllByUserId(userId).stream()
                .map(c -> new CertificateDTO(
                        c.getCourse().getId(),
                        c.getCourse().getName(),
                        c.getIssueDate(),
                        c.getCertificateUrl()))
                .collect(Collectors.toList());
    }

    @Override
    public CertificateDTO generateCertificate(Long userId, Long courseId) {
        Certificate certificate = certificateRepository
                .findByUserIdAndCourseId(userId, courseId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("User not found"));
                    Course course = courseRepository.findById(courseId)
                            .orElseThrow(() -> new RuntimeException("Course not found"));

                    Certificate newCert = new Certificate();
                    newCert.setUser(user);
                    newCert.setCourse(course);
                    newCert.setIssueDate(LocalDate.now());

                    // giả lập URL, có thể thay bằng file thật sau này
                    newCert.setCertificateUrl("https://example.com/certificates/" + userId + "-" + courseId + ".pdf");
                    return certificateRepository.save(newCert);
                });

        return new CertificateDTO(
                certificate.getCourse().getId(),
                certificate.getCourse().getName(),
                certificate.getIssueDate(),
                certificate.getCertificateUrl()
        );
    }
}
