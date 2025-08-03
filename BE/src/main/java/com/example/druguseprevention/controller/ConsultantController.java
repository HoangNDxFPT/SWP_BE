package com.example.druguseprevention.controller;

import com.example.druguseprevention.dto.*;
import com.example.druguseprevention.entity.ConsultantDetail;
import com.example.druguseprevention.repository.UserRepository;
import com.example.druguseprevention.service.ConsultantService;
//import com.example.druguseprevention.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping("/api/consultant")
@RequiredArgsConstructor
public class ConsultantController {

    private final ConsultantService consultantService;
    private final UserRepository userRepository;

    @SecurityRequirement(name = "api")
    @SecurityRequirement(name = "bearer-key")
    private Long getCurrentUserId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return consultantService.getUserIdByUsername(username);
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

    @GetMapping("/public")
    public ResponseEntity<List<ConsultantPublicProfileDto>> getAllPublicProfiles() {
        return ResponseEntity.ok(consultantService.getAllPublicConsultants());
    }
    @SecurityRequirement(name = "api")
    @SecurityRequirement(name = "bearer-key")
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ConsultantFullProfileDto>> getAllConsultants() {
        return ResponseEntity.ok(consultantService.getAllConsultantFullProfiles());
    }

    @SecurityRequirement(name = "api")
    @SecurityRequirement(name = "bearer-key")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/profile/{consultantId}")
    public ResponseEntity<Void> updateConsultantProfileByAdmin(
            @PathVariable Long consultantId,
            @RequestBody ConsultantProfileDto profileDto) {
        consultantService.updateProfile(consultantId, profileDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/public/{consultantId}")
    public ResponseEntity<ConsultantPublicProfileDto> getPublicConsultantById(
            @PathVariable Long consultantId) {
        return ResponseEntity.ok(consultantService.getPublicConsultantProfile(consultantId));
    }



//    @Operation(summary = "Upload ảnh bằng cấp")
//    @SecurityRequirement(name = "bearer-key")
//    @PostMapping(value = "/profile/upload-degree", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<?> uploadCertifiedDegreeImage(@RequestParam("file") MultipartFile file) {
//        try {
//            Long consultantId = getCurrentUserId();
//
//            String uploadDir = "uploads/certified-degree/";
//            Files.createDirectories(Paths.get(uploadDir));
//
//            String fileName = "degree_" + consultantId + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
//            Path filePath = Paths.get(uploadDir + fileName);
//            file.transferTo(filePath);
//
//            ConsultantDetail detail = consultantService.getConsultantDetailById(consultantId);
//            detail.setCertifiedDegreeImage("/uploads/certified-degree/" + fileName);
//            consultantService.saveConsultantDetail(detail);
//
//            return ResponseEntity.ok(Map.of("message", "Upload thành công", "url", detail.getCertifiedDegreeImage()));
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
//        }
//    }


}



    //    @SecurityRequirement(name = "api")
//    @SecurityRequirement(name = "bearer-key")
//    @GetMapping("/dashboard")
//    public ResponseEntity<ConsultantDashboardDto> getDashboard() {
//        return ResponseEntity.ok(consultantService.getDashboard(getCurrentUserId()));
//    }
//    @SecurityRequirement(name = "api")
//    @SecurityRequirement(name = "bearer-key")
//    @GetMapping("/appointments")
//    public ResponseEntity<List<AppointmentDTO>> getAppointments() {
//        return ResponseEntity.ok(consultantService.getAppointments(getCurrentUserId()));
//    }
//    @SecurityRequirement(name = "api")
//    @SecurityRequirement(name = "bearer-key")
//    @PostMapping("/appointments")
//    public ResponseEntity<AppointmentCreatedResponseDto> createAppointment(
//            @RequestBody CreateAppointmentDto dto) {
//        Long consultantId = getCurrentUserId();
//        return ResponseEntity.ok(consultantService.createAppointment(consultantId, dto));
//    }
//    //  Gộp upload image & certificate
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


//    @GetMapping("/public/all") // Hoặc chỉ "/public" nếu bạn muốn nó là endpoint mặc định
//    public ResponseEntity<List<ConsultantPublicProfileDto>> getAllPublicProfiles() {
//        // Bạn cần một phương thức trong ConsultantService để lấy tất cả hồ sơ công khai
//        return ResponseEntity.ok(consultantService.getAllPublicConsultantProfiles());
//    }


