package com.tasksphere.taskmanager.application.dto.auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthenticationResponse {
    private String token;
    private UserResponse user;

    @Data
    @Builder
    public static class UserResponse {
        private Long id;
        private String name;
        private String email;
        private String role;
    }
} 