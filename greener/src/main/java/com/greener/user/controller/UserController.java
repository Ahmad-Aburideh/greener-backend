package com.greener.user.controller;

import com.greener.user.dto.RegisterRequest;
import com.greener.user.dto.UserResponse;
import com.greener.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Create user (باستخدام DTO بدل Entity)
    @PostMapping
    public UserResponse createUser(@RequestBody RegisterRequest request) {
        return userService.createUser(request);
    }

    // Get all users (بيرجع DTO clean)
    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }
}