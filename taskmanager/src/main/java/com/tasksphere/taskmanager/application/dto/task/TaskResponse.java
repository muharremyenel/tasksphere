package com.tasksphere.taskmanager.application.dto.task;

import com.tasksphere.taskmanager.domain.enums.TaskPriority;
import com.tasksphere.taskmanager.domain.enums.TaskStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDateTime createdAt;
    private LocalDateTime dueDate;
    private UserSummary createdBy;
    private UserSummary assignedTo;
    private Set<UserSummary> collaborators;

    @Data
    @Builder
    public static class UserSummary {
        private Long id;
        private String name;
        private String email;
    }
} 