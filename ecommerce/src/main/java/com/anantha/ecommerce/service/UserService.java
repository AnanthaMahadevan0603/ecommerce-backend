package com.anantha.ecommerce.service;

import com.anantha.ecommerce.dto.UserLoginDTO;
import com.anantha.ecommerce.dto.UserRegisterDTO;
import com.anantha.ecommerce.dto.UserResponseDTO;

public interface UserService {
    /**
     * Register a new user with a roleId provided in dto.
     * Should validate unique email.
     */
    UserResponseDTO register(UserRegisterDTO dto);

    /**
     * Login a user. JWT will be added in a later phase.
     * For now, returns user info on success.
     */
    UserResponseDTO login(UserLoginDTO dto);
}
