package com.tasksphere.taskmanager.application.dto.notification;

import com.tasksphere.taskmanager.domain.enums.NotificationType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponse {
    private Long id;
    private String message;
    private NotificationType type;
    private Long taskId;
    private boolean read;
    private LocalDateTime createdAt;
} 