package com.tasksphere.taskmanager.application.service;

import com.tasksphere.taskmanager.application.dto.user.CreateUserRequest;
import com.tasksphere.taskmanager.application.dto.user.UpdateUserRequest;
import com.tasksphere.taskmanager.application.dto.user.UserResponse;
import java.util.List;

public interface UserService {
    List<UserResponse> getUsers();
    UserResponse createUser(CreateUserRequest request);
    UserResponse updateUser(Long id, UpdateUserRequest request);
    void deleteUser(Long id);
} 