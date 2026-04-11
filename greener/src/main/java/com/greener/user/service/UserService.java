package com.greener.user.service;

import com.greener.user.dto.LoginRequest;
import com.greener.user.dto.RegisterRequest;
import com.greener.user.dto.UserResponse;
import com.greener.user.entity.Role;
import com.greener.user.entity.User;
import com.greener.user.mapper.UserMapper;
import com.greener.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    // 🔹 Create User
    public UserResponse createUser(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = userMapper.toEntity(request);

        user.setPassword(request.getPassword()); // ❌ بدون hashing
        user.setRole(Role.USER);
        user.setPoints(0);

        return userMapper.toResponse(userRepository.save(user));
    }

    // 🔹 Get All Users
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }

    // 🔹 Register
    public UserResponse register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = userMapper.toEntity(request);

        user.setPassword(request.getPassword()); // ❌ بدون hashing
        user.setRole(Role.USER);
        user.setPoints(0);

        return userMapper.toResponse(userRepository.save(user));
    }

    // 🔹 Login
    public UserResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        // 🔥 مقارنة مباشرة بدون BCrypt
        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return userMapper.toResponse(user);
    }
}