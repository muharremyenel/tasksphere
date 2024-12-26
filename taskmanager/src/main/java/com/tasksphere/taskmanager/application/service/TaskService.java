package com.tasksphere.taskmanager.application.service;

import com.tasksphere.taskmanager.application.dto.task.CreateTaskRequest;
import com.tasksphere.taskmanager.application.dto.task.TaskResponse;
import com.tasksphere.taskmanager.application.dto.task.UpdateTaskStatusRequest;
import com.tasksphere.taskmanager.application.dto.task.UpdateTaskRequest;
import com.tasksphere.taskmanager.application.dto.task.TaskSearchRequest;

import java.util.List;

public interface TaskService {
    TaskResponse createTask(CreateTaskRequest request);
    List<TaskResponse> getMyTasks();
    List<TaskResponse> getCreatedTasks();
    TaskResponse updateTaskStatus(Long taskId, UpdateTaskStatusRequest request);
    TaskResponse updateTask(Long taskId, UpdateTaskRequest request);
    void deleteTask(Long taskId);
    List<TaskResponse> searchTasks(TaskSearchRequest request);
} 