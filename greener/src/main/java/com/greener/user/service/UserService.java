package com.greener.user.service;

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

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserResponse createUser(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("Email already exists");
        }

        User user = userMapper.toEntity(request);

        user.setPassword(request.getPassword());
        user.setRole(Role.USER);
        user.setPoints(0);

        return userMapper.toResponse(userRepository.save(user));
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }

    public UserResponse register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("Email already exists");
        }

        User user = userMapper.toEntity(request);

        user.setPassword(request.getPassword());
        user.setRole(Role.USER);
        user.setPoints(0);

        return userMapper.toResponse(userRepository.save(user));
    }

    public UserResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid credentials"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new BadRequestException("Invalid credentials");
        }

        return userMapper.toResponse(user);
    }
}