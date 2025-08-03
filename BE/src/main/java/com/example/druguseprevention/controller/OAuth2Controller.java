package com.example.druguseprevention.controller;

import com.example.druguseprevention.dto.UserResponse;
import com.example.druguseprevention.entity.User;
import com.example.druguseprevention.repository.AuthenticationRepository;
import com.example.druguseprevention.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class OAuth2Controller {

    private final TokenService tokenService;

    private final AuthenticationRepository authenticationRepository;

    @GetMapping("/oauth2/success")
    public ResponseEntity<UserResponse> getOAuth2Success(Authentication authentication) {
        DefaultOAuth2User oauth2User = (DefaultOAuth2User) authentication.getPrincipal();
        String email = oauth2User.getAttribute("email");

        User user = authenticationRepository.findUserByEmail(email);
        String token = tokenService.generateToken(user);

        UserResponse response = new UserResponse();
        response.setToken(token);
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setUserName(user.getUsername());
        response.setRole(user.getRole());

        return ResponseEntity.ok(response);
    }
}

