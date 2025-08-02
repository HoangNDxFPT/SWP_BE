package com.example.druguseprevention.service;

import com.example.druguseprevention.dto.*;
import com.example.druguseprevention.entity.User;
import com.example.druguseprevention.enums.Role;
import com.example.druguseprevention.exception.exceptions.AuthenticationException;
import com.example.druguseprevention.exception.exceptions.BadRequestException;
import com.example.druguseprevention.repository.AuthenticationRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class AuthenticationService implements UserDetailsService {

    private final AuthenticationRepository authenticationRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final TokenService tokenService;
    private final EmailService emailService;
    private final TemplateEngine templateEngine;
    // AuthenticationManager cần @Lazy để tránh circular dependency
    @Autowired
    @Lazy
    private AuthenticationManager authenticationManager;

    public User register(RegisterRequest registerRequest) {
        if (authenticationRepository.findUserByUserName(registerRequest.getUserName()) != null) {
            throw new BadRequestException("Username already exists!");
        }

        if (authenticationRepository.findUserByEmail(registerRequest.getEmail()) != null) {
            throw new BadRequestException("Email already exists!");
        }

        User user = modelMapper.map(registerRequest, User.class);
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(Role.MEMBER);
        user.setDeleted(false);
        user.setActive(false);

        // Tạo activation token
        String activationToken = UUID.randomUUID().toString();
        user.setActivationToken(activationToken);

        user = authenticationRepository.save(user);
        sendActivationEmail(user);
        return user;
    }

    private void sendActivationEmail(User user){
        EmailDetail emailDetail = new EmailDetail();
        emailDetail.setRecipient(user.getEmail());
        emailDetail.setSubject("Activate your account");

        Context context = new Context();
        context.setVariable("name", user.getFullName());
        context.setVariable("button", "Activate Account");
        context.setVariable("link", "http://localhost:5173/activate?token=" + user.getActivationToken()); // đường link frontend

        String html = templateEngine.process("emailtemplate", context);
        emailService.sendHtmlEmail(emailDetail, html);
    }

    public void resendActivationEmail(String email) {
        User user = authenticationRepository.findUserByEmail(email);
        if (user == null) {
            throw new BadRequestException("User not found with this email.");
        }

        if (user.isActive()) {
            throw new BadRequestException("Account is already activated.");
        }

        // Tạo token mới
        String newToken = UUID.randomUUID().toString();
        user.setActivationToken(newToken);
        authenticationRepository.save(user);

        // Gửi lại email
        sendActivationEmail(user);
    }

    public void activateAccount(String token) {
        User user = authenticationRepository.findByActivationToken(token);
        if (user == null) {
            throw new BadRequestException("Invalid activation token.");
        }

        if (user.isActive()) {
            throw new BadRequestException("Account is already activated.");
        }

        user.setActive(true);
        user.setActivationToken(null); // Xóa token sau khi kích hoạt
        authenticationRepository.save(user);
    }

    public UserResponse login (LoginRequest loginRequest){
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequest.getUserName(),
                    loginRequest.getPassword()
            ));

        }catch (Exception e){
            throw new AuthenticationException("Username or Password not valid!");
        }
        User user = authenticationRepository.findUserByUserName(loginRequest.getUserName());

        // Kiểm tra user có bị xóa không
        if (user.isDeleted()) {
            throw new AuthenticationException("Account has been deleted.");
        }

        // Kiểm tra tài khoản đã được kích hoạt chưa
        if(!user.isActive()){
            throw new BadRequestException("Account is not activated. Please check your email.");
        }
        UserResponse userResponse = modelMapper.map(user, UserResponse.class);
        String token = tokenService.generateToken(user);
        userResponse.setToken(token);
        return userResponse ;
    }

    public void changePassword(ChangePasswordRequest request){
        //Dùng để lấy thông tin người dùng hiện tại đang đăng nhập
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!passwordEncoder.matches(request.getOldPassword(), user.getPassword())){
            throw new AuthenticationException("Old password is incorrect.");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        authenticationRepository.save(user);
    }

    public void forgotPassword (ForgotPasswordRequest request){
        User user = authenticationRepository.findUserByEmail(request.getEmail());
        if (user == null) {
            throw new AuthenticationException("User not found with this email.");
        }

        String token = tokenService.generateToken(user);
        EmailDetail detail = new EmailDetail();
        detail.setRecipient(user.getEmail());
        detail.setSubject("Reset your password");

        Context context = new Context();
        context.setVariable("name", user.getFullName());
        context.setVariable("button", "Reset Password");
        context.setVariable("link", "http://localhost:5173/reset-password?token=" + token); // đường link frontend

        String html = templateEngine.process("resetpasswordtemplate", context);
        emailService.sendHtmlEmail(detail, html);

    }

    public void resetPassword(ResetPasswordRequest request) {
        User user;
        try {
            user = tokenService.extractAccount(request.getToken());
        } catch (Exception e) {
            throw new AuthenticationException("Invalid or expired token.");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        authenticationRepository.save(user);
    }


    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        return authenticationRepository.findUserByUserName(userName);
    }
    public User createUserByAdmin(CreateUserRequest request) {
        User user = new User();
        user.setUserName(request.getUserName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddress(request.getAddress());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setGender(request.getGender());
        user.setActive(true);

        // Admin chọn role
        user.setRole(request.getRole() != null ? request.getRole() : Role.MEMBER);

        return authenticationRepository.save(user);
    }



    

}
