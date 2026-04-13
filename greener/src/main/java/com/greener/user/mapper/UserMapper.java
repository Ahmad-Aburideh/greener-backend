package com.greener.user.mapper;

import com.greener.user.dto.RegisterRequest;
import com.greener.user.dto.UserResponse;
import com.greener.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "points", ignore = true)
    User toEntity(RegisterRequest request);

    default UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .points(user.getPoints())
                .build();
    }
}