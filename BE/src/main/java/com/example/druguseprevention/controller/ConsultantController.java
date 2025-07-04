package com.example.druguseprevention.controller;

import com.example.druguseprevention.dto.*;
import com.example.druguseprevention.repository.UserRepository;
import com.example.druguseprevention.service.ConsultantService;
//import com.example.druguseprevention.service.FileStorageService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;


@RestController
@RequestMapping("/api/consultant")
@RequiredArgsConstructor
public class ConsultantController {

    private final ConsultantService consultantService;
    private final UserRepository userRepository;
//    private final FileStorageService fileStorageService;
@SecurityRequirement(name = "api")
@SecurityRequirement(name = "bearer-key")
    private Long getCurrentUserId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return consultantService.getUserIdByUsername(username);
    }
    @SecurityRequirement(name = "api")
    @SecurityRequirement(name = "bearer-key")
    @GetMapping("/dashboard")
    public ResponseEntity<ConsultantDashboardDto> getDashboard() {
        return ResponseEntity.ok(consultantService.getDashboard(getCurrentUserId()));
    }
    @SecurityRequirement(name = "api")
    @SecurityRequirement(name = "bearer-key")
    @GetMapping("/appointments")
    public ResponseEntity<List<AppointmentDTO>> getAppointments() {
        return ResponseEntity.ok(consultantService.getAppointments(getCurrentUserId()));
    }
    @SecurityRequirement(name = "api")
    @SecurityRequirement(name = "bearer-key")
    @PostMapping("/appointments")
    public ResponseEntity<AppointmentCreatedResponseDto> createAppointment(
            @RequestBody CreateAppointmentDto dto) {
        Long consultantId = getCurrentUserId();
        return ResponseEntity.ok(consultantService.createAppointment(consultantId, dto));
    }

    @SecurityRequirement(name = "api")
    @SecurityRequirement(name = "bearer-key")
    @PutMapping("/profile")
    public ResponseEntity<Void> updateConsultantProfile(@RequestBody ConsultantProfileDto profileDto) {
        consultantService.updateProfile(getCurrentUserId(), profileDto);
        return ResponseEntity.ok().build();
    }

    @SecurityRequirement(name = "api")
    @SecurityRequirement(name = "bearer-key")
    @GetMapping("/all-profiles")
    public ResponseEntity<List<UserProfileDto>> getAllMemberProfiles() {
        return ResponseEntity.ok(consultantService.getAllMemberProfiles());
    }
    @SecurityRequirement(name = "api")
    @SecurityRequirement(name = "bearer-key")
    @GetMapping("/profile")
    public ResponseEntity<ConsultantProfileDto> getConsultantProfile() {
        return ResponseEntity.ok(consultantService.getProfile(getCurrentUserId()));
    }

//    // ✅ Gộp upload image & certificate
//    @PostMapping("/profile/upload")
//    public ResponseEntity<Map<String, String>> uploadFile(
//            @RequestParam("type") String type,
//            @RequestParam("file") MultipartFile file) {
//        try {
//            String fileUrl = fileStorageService.storeFile(file, type);
//            return ResponseEntity.ok(Map.of("url", fileUrl));
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
//        }
//    }

    //    //  Gộp tải file (image hoặc certificate)
//    @GetMapping("/uploads/{type}/{fileName:.+}")
//    public ResponseEntity<Resource> getUploadedFile(
//            @PathVariable String type,
//            @PathVariable String fileName) {
//        try {
//            Resource resource = fileStorageService.loadFile(type, fileName);
//            String contentType = fileStorageService.getContentType(resource);
//
//            return ResponseEntity.ok()
//                    .contentType(MediaType.parseMediaType(contentType))
//                    .body(resource);
//        } catch (IOException e) {
//            return ResponseEntity.notFound().build();
//        }
//    }
//}
//  PUBLIC: Lấy thông tin profile của 1 consultant bất kỳ (không cần đăng nhập)

    @GetMapping("/public/{consultantId}")
    public ResponseEntity<ConsultantPublicProfileDto> getPublicProfile(@PathVariable Long consultantId) {
        return ResponseEntity.ok(consultantService.getPublicConsultantProfile(consultantId));
    }
//    @GetMapping("/public/all") // Hoặc chỉ "/public" nếu bạn muốn nó là endpoint mặc định
//    public ResponseEntity<List<ConsultantPublicProfileDto>> getAllPublicProfiles() {
//        // Bạn cần một phương thức trong ConsultantService để lấy tất cả hồ sơ công khai
//        return ResponseEntity.ok(consultantService.getAllPublicConsultantProfiles());
//    }
}

