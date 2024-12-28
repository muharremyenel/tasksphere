package com.tasksphere.taskmanager.application.service;

import com.tasksphere.taskmanager.application.dto.auth.AuthenticationRequest;
import com.tasksphere.taskmanager.application.dto.auth.AuthenticationResponse;

public interface AuthService {
    AuthenticationResponse authenticate(AuthenticationRequest request);
    void logout(String token);
} 