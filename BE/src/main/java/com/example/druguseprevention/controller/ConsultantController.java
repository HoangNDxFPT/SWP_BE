package com.example.druguseprevention.controller;

import com.example.druguseprevention.dto.*;
import com.example.druguseprevention.enums.Role;
import com.example.druguseprevention.repository.UserRepository;
import com.example.druguseprevention.service.ConsultantService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;


@SecurityRequirement(name = "api")
@SecurityRequirement(name = "bearer-key")
@RestController
@RequestMapping("/api/consultant")
@RequiredArgsConstructor
public class ConsultantController {

    private final ConsultantService consultantService;
    private final UserRepository userRepository;

    @GetMapping("/{id}/available-slots")
    public ResponseEntity<ConsultantAvailableSlotsResponse> getAvailableSlots(
            @PathVariable("id") Long consultantId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(consultantService.getAvailableSlots(consultantId, date));
    }


    @GetMapping("/profile/{consultantId}")
    public ResponseEntity<ConsultantProfileDto> getConsultantProfile(@PathVariable Long consultantId) {
        ConsultantProfileDto profile = consultantService.getProfile(consultantId);
        return ResponseEntity.ok(profile);
    }

    // ✅ Lấy userId từ token (username nằm trong token)
    private Long getCurrentUserId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return consultantService.getUserIdByUsername(username);
    }

    // CN01: Dashboard tổng quan
    @GetMapping("/dashboard")
    public ResponseEntity<ConsultantDashboardDto> getDashboard() {
        return ResponseEntity.ok(consultantService.getDashboard(getCurrentUserId()));
    }

    // CN02: Xem danh sách lịch hẹn
    @GetMapping("/appointments")
    public ResponseEntity<List<AppointmentDTO>> getAppointments() {
        return ResponseEntity.ok(consultantService.getAppointments(getCurrentUserId()));
    }

    // CN02: Xác nhận cuộc hẹn
    @PutMapping("/appointments/{id}/confirm")
    public ResponseEntity<Void> confirmAppointment(@PathVariable Long id) {
        consultantService.confirmAppointment(id);
        return ResponseEntity.ok().build();
    }

    // CN02: Từ chối cuộc hẹn
    @PutMapping("/appointments/{id}/reject")
    public ResponseEntity<Void> rejectAppointment(@PathVariable Long id) {
        consultantService.rejectAppointment(id);
        return ResponseEntity.ok().build();
    }

    // CN04: Xem phân tích khảo sát của user
//    @GetMapping("/survey-analysis/{userId}")
//    public ResponseEntity<List<SurveyAnalysisDto>> getSurveyAnalysis(@PathVariable Long userId) {
//        return ResponseEntity.ok(consultantService.getSurveyAnalysis(userId));
//    }

    // CN03: Ghi chú hồ sơ người dùng
    @PutMapping("/user/{userId}/note")
    public ResponseEntity<Void> updateUserNote(@PathVariable Long userId, @RequestBody String note) {
        consultantService.updateUserNote(userId, note);
        return ResponseEntity.ok().build();
    }

    // CN05: Gợi ý hành động can thiệp
    @PostMapping("/user/{userId}/suggestion")
    public ResponseEntity<Void> suggestAction(
            @PathVariable Long userId,
            @RequestBody ConsultantSuggestionDto suggestionDto) {
        consultantService.suggestAction(userId, suggestionDto);
        return ResponseEntity.ok().build();
    }

    // CN06: Cập nhật hồ sơ tư vấn viên (dùng token)
    @PutMapping("/profile")
    public ResponseEntity<Void> updateConsultantProfile(@RequestBody ConsultantProfileDto profileDto) {
        consultantService.updateProfile(getCurrentUserId(), profileDto);
        return ResponseEntity.ok().build();
    }

    // CN07: Thống kê tư vấn (dùng token)
    @GetMapping("/statistics")
    public ResponseEntity<ConsultationStatisticsDto> getStatistics() {
        return ResponseEntity.ok(consultantService.getStatistics(getCurrentUserId()));
    }

    // CN02: Cập nhật trạng thái và ghi chú cuộc hẹn
    @PutMapping("/appointments/{id}/status")
    public ResponseEntity<Void> updateAppointmentStatus(
            @PathVariable Long id,
            @RequestBody UpdateAppointmentStatusDto statusDto) {
        consultantService.updateAppointmentStatus(id, statusDto);
        return ResponseEntity.ok().build();
    }
    // CN08: Tạo lịch hẹn với chuyên viên
    // CN08: Tạo lịch hẹn với chuyên viên
    @PostMapping("/appointments")
    public ResponseEntity<AppointmentCreatedResponseDto> createAppointment(
            @RequestBody CreateAppointmentDto dto) {
        Long consultantId = getCurrentUserId(); // từ token
        return ResponseEntity.ok(consultantService.createAppointment(consultantId, dto));
    }

    @PutMapping("/appointments/{id}/note")
    public ResponseEntity<Void> updateAppointmentNote(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String note = body.get("note");
        consultantService.updateAppointmentNote(id, note);
        return ResponseEntity.ok().build();
    }
    // CN10: Lấy danh sách tất cả người dùng (role = MEMBER)
    @GetMapping("/all-profiles")
    public ResponseEntity<List<UserProfileDto>> getAllMemberProfiles() {
        return ResponseEntity.ok(consultantService.getAllMemberProfiles());
    }
    @GetMapping("/consultants")
    public ResponseEntity<List<ConsultantProfileResponse>> getAllConsultants() {
        return ResponseEntity.ok(
                userRepository.findByRoleAndDeletedFalse(Role.CONSULTANT)
                        .stream()
                        .map(user -> ConsultantProfileResponse.builder()
                                .id(user.getId())
                                .fullName(user.getFullName())
                                .email(user.getEmail())
                                .phone(user.getPhoneNumber())
                                .build())
                        .collect(Collectors.toList())
        );

    }
        // CNxx: Lấy hồ sơ tư vấn viên đang đăng nhập
        @GetMapping("/profile")
        public ResponseEntity<ConsultantProfileDto> getConsultantProfile() {
            return ResponseEntity.ok(consultantService.getProfile(getCurrentUserId()));
        }
    @PostMapping("/upload-certificate")
    public ResponseEntity<String> uploadCertificateImage(@RequestParam("file") MultipartFile file) {
        try {
            // Tạo thư mục nếu chưa có
            String uploadDir = "uploads/certificates";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            // Đặt tên file duy nhất
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path path = Paths.get(uploadDir, fileName);
            file.transferTo(path);

            // Trả về đường dẫn (có thể là URL nếu bạn dùng cloud/CDN)
            String fileUrl = "/uploads/certificates/" + fileName;
            return ResponseEntity.ok(fileUrl);

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Upload failed: " + e.getMessage());
        }
    }
        @PostMapping("/profile/image")
        public ResponseEntity<?> uploadCertifiedDegreeImage(@RequestParam("file") MultipartFile file) {
            try {
                Path uploadPath = Paths.get("uploads/consultants");
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                String fileUrl = "/uploads/consultants/" + fileName;
                return ResponseEntity.ok(Map.of("url", fileUrl));
            } catch (IOException e) {
                return ResponseEntity.status(500).body("Upload failed: " + e.getMessage());
            }
        }
    @GetMapping("/uploads/certificates/{fileName:.+}")
    public ResponseEntity<Resource> getCertificateImage(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get("uploads/certificates").resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                String contentType = Files.probeContentType(filePath);
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType != null ? contentType : "application/octet-stream"))
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
        @GetMapping("/uploads/consultants/{fileName:.+}")
        public ResponseEntity<Resource> getConsultantImage(@PathVariable String fileName) {
            try {
                Path filePath = Paths.get("uploads/consultants").resolve(fileName).normalize();
                Resource resource = new UrlResource(filePath.toUri());

                if (resource.exists()) {
                    String contentType = Files.probeContentType(filePath);
                    return ResponseEntity.ok()
                            .contentType(MediaType.parseMediaType(contentType != null ? contentType : "application/octet-stream"))
                            .body(resource);
                } else {
                    return ResponseEntity.notFound().build();
                }

            } catch (IOException e) {
                return ResponseEntity.internalServerError().build();
            }
        }


    }

    










