package com.greener.auth.controller;

import com.greener.api.ApiResponse;
import com.greener.auth.entity.VerificationToken;
import com.greener.auth.repository.VerificationTokenRepository;
import com.greener.user.dto.LoginRequest;
import com.greener.user.dto.RegisterRequest;
import com.greener.user.dto.UserResponse;
import com.greener.user.entity.User;
import com.greener.user.repository.UserRepository;
import com.greener.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final VerificationTokenRepository verificationTokenRepository;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ApiResponse<UserResponse> register(@RequestBody RegisterRequest request) {
        return ApiResponse.<UserResponse>builder()
                .status("success")
                .data(userService.register(request))
                .build();
    }

    @PostMapping("/login")
    public ApiResponse<String> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.<String>builder()
                .status("success")
                .data(userService.login(request))
                .build();
    }

    @GetMapping("/verify")
    public String verify(@RequestParam String token) {

        VerificationToken verificationToken =
                verificationTokenRepository.findByToken(token)
                        .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return "<h2>Token expired ❌</h2>";
        }

        User user = verificationToken.getUser();
        user.setVerified(true);
        userRepository.save(user);

        return """
        <html>
        <head>
            <title>Greener - Verified</title>
        </head>
        <body style="font-family: Arial; text-align: center; padding: 40px;">
            
            <img src="https://i.imgur.com/TiqHF9K.png" width="120"/>

            <h1 style="color:#2ecc71;">Email Verified 🎉</h1>

            <p>Your account has been successfully verified.</p>

            <a href="http://localhost:3000" 
               style="display:inline-block; padding:12px 25px; background:#2ecc71; color:white; 
                      text-decoration:none; border-radius:8px; margin-top:20px;">
                Go to App
            </a>

        </body>
        </html>
    """;
    }
}