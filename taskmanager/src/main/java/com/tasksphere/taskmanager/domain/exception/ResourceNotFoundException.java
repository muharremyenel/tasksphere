package com.tasksphere.taskmanager.domain.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends TaskSphereException {
    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
} 