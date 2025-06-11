package com.example.druguseprevention.api;

import com.example.druguseprevention.dto.ProfileDTO;
import com.example.druguseprevention.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @SecurityRequirement(name = "api")
    @GetMapping
    public ResponseEntity<ProfileDTO> getProfile() {
        return ResponseEntity.ok(userService.getProfile());
    }

    @SecurityRequirement(name = "api")
    @PutMapping
    public ResponseEntity<?> updateProfile(@RequestBody ProfileDTO dto) {
        userService.updateProfile(dto);
        return ResponseEntity.ok("Updated");
    }

    @SecurityRequirement(name = "api")
    @GetMapping("/{id}")
    public ResponseEntity<ProfileDTO> getProfileById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getProfileById(id));
    }
}
