package com.tasksphere.taskmanager.application.dto.user;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UpdateUserRequest {
    private String name;
    
    @Email(message = "Invalid email format")
    private String email;
    
    private String password;
    private Long teamId;
} 