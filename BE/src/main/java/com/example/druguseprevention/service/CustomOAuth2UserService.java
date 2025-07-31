package com.example.druguseprevention.service;

import com.example.druguseprevention.entity.User;
import com.example.druguseprevention.enums.Gender;
import com.example.druguseprevention.enums.Role;
import com.example.druguseprevention.repository.AuthenticationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Autowired
    private AuthenticationRepository authenticationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("CustomOAuth2UserService.loadUser() called");
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        User user = authenticationRepository.findUserByEmail(email);

        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setFullName(name);
            user.setUserName(email);
            user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
            user.setGender(Gender.MALE);
            user.setRole(Role.MEMBER);
            try {
                authenticationRepository.save(user);
            } catch (Exception e) {
                System.out.println(" Failed to save user: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole())),
                oAuth2User.getAttributes(),
                "email"
        );
    }
}

