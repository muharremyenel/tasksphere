package com.tasksphere.taskmanager.application.service.impl;

import com.tasksphere.taskmanager.application.service.AuthService;
import com.tasksphere.taskmanager.application.service.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final TokenBlacklistService tokenBlacklistService;

    @Override
    public void logout(String token) {
        tokenBlacklistService.addToBlacklist(token);
    }
} 