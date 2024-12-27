package com.tasksphere.taskmanager.application.dto.notification;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponse {
    private Long id;
    private String title;
    private String content;
    private boolean read;
    private LocalDateTime createdAt;
    private TaskSummary task;

    @Data
    @Builder
    public static class TaskSummary {
        private Long id;
        private String title;
    }
} 