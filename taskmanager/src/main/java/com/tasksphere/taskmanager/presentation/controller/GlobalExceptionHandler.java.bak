package com.tasksphere.taskmanager.presentation.controller;

import com.tasksphere.taskmanager.domain.exception.TaskSphereException;
import com.tasksphere.taskmanager.presentation.response.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TaskSphereException.class)
    public ResponseEntity<ErrorResponse> handleTaskSphereException(
            TaskSphereException ex, WebRequest request) {
        
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(ex.getStatus().value())
                .error(ex.getStatus().getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getDescription(false))
                .build();

        return new ResponseEntity<>(error, ex.getStatus());
    }
} 