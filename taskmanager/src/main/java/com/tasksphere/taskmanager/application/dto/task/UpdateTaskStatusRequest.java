package com.tasksphere.taskmanager.application.dto.task;

import com.tasksphere.taskmanager.domain.enums.TaskStatus;
import lombok.Data;

@Data
public class UpdateTaskStatusRequest {
    private TaskStatus status;
} 