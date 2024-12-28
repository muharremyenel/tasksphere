package com.tasksphere.taskmanager.application.service.impl;

import com.tasksphere.taskmanager.application.dto.auth.AuthenticationRequest;
import com.tasksphere.taskmanager.application.dto.auth.AuthenticationResponse;
import com.tasksphere.taskmanager.application.service.AuthService;
import com.tasksphere.taskmanager.application.service.JwtService;
import com.tasksphere.taskmanager.domain.entity.User;
import com.tasksphere.taskmanager.infrastructure.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );

        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        String token = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
            .token(token)
            .user(mapToUserResponse(user))
            .build();
    }

    @Override
    public void logout(String token) {
        jwtService.invalidateToken(token);
    }

    private AuthenticationResponse.UserResponse mapToUserResponse(User user) {
        return AuthenticationResponse.UserResponse.builder()
            .id(user.getId())
            .name(user.getName())
            .email(user.getEmail())
            .role(user.getRole().name())
            .build();
    }
} 