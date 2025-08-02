package com.example.druguseprevention.controller;

import com.example.druguseprevention.dto.*;
import com.example.druguseprevention.entity.User;
import com.example.druguseprevention.service.AuthenticationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@CrossOrigin("*")// cho phép tất cả truy cập
public class AuthenticationController {


    private final AuthenticationService authenticationService;

    @PostMapping("/api/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest){
        User newUser = authenticationService.register(registerRequest);
        return ResponseEntity.ok(newUser);
    }

    @PostMapping("/api/login")
    public ResponseEntity login (@RequestBody LoginRequest loginRequest){
        UserResponse userResponse = authenticationService.login(loginRequest);
        return ResponseEntity.ok(userResponse);
    }

    @SecurityRequirement(name = "api")
    @PostMapping("/api/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        authenticationService.changePassword(request);
        return ResponseEntity.ok("Password changed successfully.");
    }

    @PostMapping("/api/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        authenticationService.forgotPassword(request);
        return ResponseEntity.ok("Password reset email sent.");
    }

    @PostMapping("/api/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        authenticationService.resetPassword(request);
        return ResponseEntity.ok("Password has been reset successfully.");
    }

    @SecurityRequirement(name = "api")
    @PostMapping("/api/logout")
    public ResponseEntity<?> logout() {
        // Client sẽ tự xóa token, backend chỉ phản hồi xác nhận
        return ResponseEntity.ok("Logout successfully!");
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activateAccount(@RequestParam String token) {
        authenticationService.activateAccount(token);
        return ResponseEntity.ok("Account activated successfully! You can now login.");
    }

    @PostMapping("/resend-activation")
    public ResponseEntity<String> resendActivationEmail(@RequestParam String email) {
        authenticationService.resendActivationEmail(email);
        return ResponseEntity.ok("Activation email has been resent. Please check your inbox.");
    }

}
