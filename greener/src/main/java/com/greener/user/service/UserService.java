package com.greener.user.service;

import com.greener.auth.entity.VerificationToken;
import com.greener.auth.repository.VerificationTokenRepository;
import com.greener.auth.service.JwtService;
import com.greener.email.EmailService;
import com.greener.exception.BadRequestException;
import com.greener.user.dto.LoginRequest;
import com.greener.user.dto.RegisterRequest;
import com.greener.user.dto.UserResponse;
import com.greener.user.entity.Role;
import com.greener.user.entity.User;
import com.greener.user.mapper.UserMapper;
import com.greener.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final VerificationTokenRepository verificationTokenRepository;
    private final EmailService emailService;

    public UserResponse createUser(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("Email already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setRole(Role.USER);
        user.setPoints(0);
        user.setVerified(false);

        User savedUser = userRepository.save(user);

        return userMapper.toResponse(savedUser);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }

    public UserResponse register(RegisterRequest request) {

        User existingUser = userRepository.findByEmail(request.getEmail()).orElse(null);

        // 🔥 إذا المستخدم موجود
        if (existingUser != null) {

            if (existingUser.isVerified()) {
                throw new BadRequestException("Email already registered");
            }

            String token = UUID.randomUUID().toString();

            VerificationToken verificationToken = new VerificationToken();
            verificationToken.setToken(token);
            verificationToken.setUser(existingUser);
            verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));

            verificationTokenRepository.save(verificationToken);

            // 🔥 FIX هنا
            try {
                emailService.sendVerificationEmail(existingUser.getEmail(), token);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return userMapper.toResponse(existingUser);
        }

        // 🔥 user جديد
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setRole(Role.USER);
        user.setPoints(0);
        user.setVerified(false);

        User savedUser = userRepository.save(user);

        String token = UUID.randomUUID().toString();

        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(savedUser);
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));

        verificationTokenRepository.save(verificationToken);

        System.out.println("EMAIL: " + savedUser.getEmail());

        // 🔥 FIX هنا
        try {
            emailService.sendVerificationEmail(savedUser.getEmail(), token);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return userMapper.toResponse(savedUser);
    }

    public String login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        if (!user.isVerified()) {
            throw new RuntimeException("Please verify your email first");
        }

        return jwtService.generateToken(user);
    }

    public UserResponse getUserByEmail(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return userMapper.toResponse(user);
    }
}