package com.tasksphere.taskmanager.domain.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class TaskSphereException extends RuntimeException {
    private final HttpStatus status;
    private final String message;

    public TaskSphereException(String message, HttpStatus status) {
        super(message);
        this.message = message;
        this.status = status;
    }
} 