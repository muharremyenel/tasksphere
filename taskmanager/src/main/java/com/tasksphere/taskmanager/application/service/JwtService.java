package com.tasksphere.taskmanager.application.service;

import com.tasksphere.taskmanager.domain.entity.User;

public interface JwtService {
    String generateToken(User user);
    String extractUsername(String token);
    boolean isTokenValid(String token);
    void invalidateToken(String token);
} 