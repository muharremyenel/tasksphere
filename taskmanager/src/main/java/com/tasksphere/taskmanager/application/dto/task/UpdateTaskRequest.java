package com.tasksphere.taskmanager.application.dto.task;

import com.tasksphere.taskmanager.domain.enums.TaskPriority;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class UpdateTaskRequest {
    private String title;
    private String description;
    private TaskPriority priority;
    private LocalDateTime dueDate;
    private Long assignedToId;
    private Set<Long> collaboratorIds;
} 