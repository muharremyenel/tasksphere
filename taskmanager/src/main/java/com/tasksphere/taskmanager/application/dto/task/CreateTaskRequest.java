package com.tasksphere.taskmanager.application.dto.task;

import com.tasksphere.taskmanager.domain.enums.TaskPriority;
import com.tasksphere.taskmanager.domain.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class CreateTaskRequest {
    @NotBlank(message = "Title is required")
    private String title;
    
    private String description;
    
    private TaskStatus status; // Opsiyonel, null ise TODO olacak
    
    @NotNull(message = "Priority is required")
    private TaskPriority priority;
    
    private LocalDateTime dueDate;
    
    private Long assignedToId;
    
    private Long categoryId;
    
    private Set<Long> tagIds;
} 