package com.anantha.ecommerce.mapper;

import com.anantha.ecommerce.dto.UserResponseDTO;
import com.anantha.ecommerce.entity.User;

public class UserMapper {

    public static UserResponseDTO toDTO(User user, String token) {
        if (user == null) return null;

        return UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole() != null ? user.getRole().getName() : null)
                .token(token)
                .build();
    }
}
