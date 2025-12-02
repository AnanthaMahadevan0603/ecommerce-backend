package com.anantha.ecommerce.controller;

import com.anantha.ecommerce.dto.UserLoginDTO;
import com.anantha.ecommerce.dto.UserRegisterDTO;
import com.anantha.ecommerce.dto.UserResponseDTO;
import com.anantha.ecommerce.entity.User;
import com.anantha.ecommerce.repository.UserRepository;
import com.anantha.ecommerce.security.JWTUtil;
import com.anantha.ecommerce.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserRegisterDTO dto) {
        UserResponseDTO created = userService.register(dto);
        return ResponseEntity.ok(created);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseDTO> login(@Valid @RequestBody UserLoginDTO dto) {
        // authenticate using AuthenticationManager so credentials are validated by auth system
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
        );

        // authenticated -> generate token
        // load user to get role
        User user = userRepository.findByEmail(dto.getEmail()).orElseThrow();
        String role = user.getRole() != null ? user.getRole().getName() : "USER";
        String token = jwtUtil.generateToken(user.getEmail(), role);

        UserResponseDTO resp = UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(role)
                .token(token)
                .build();

        return ResponseEntity.ok(resp);
    }
}
