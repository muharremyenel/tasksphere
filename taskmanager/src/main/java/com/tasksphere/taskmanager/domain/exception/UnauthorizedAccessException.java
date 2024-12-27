package com.tasksphere.taskmanager.domain.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedAccessException extends TaskSphereException {
    public UnauthorizedAccessException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
} 