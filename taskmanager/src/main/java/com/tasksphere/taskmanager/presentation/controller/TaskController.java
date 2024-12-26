package com.tasksphere.taskmanager.presentation.controller;

import com.tasksphere.taskmanager.application.dto.task.CreateTaskRequest;
import com.tasksphere.taskmanager.application.dto.task.TaskResponse;
import com.tasksphere.taskmanager.application.dto.task.UpdateTaskStatusRequest;
import com.tasksphere.taskmanager.application.dto.task.UpdateTaskRequest;
import com.tasksphere.taskmanager.application.dto.task.TaskSearchRequest;
import com.tasksphere.taskmanager.application.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@RequestBody CreateTaskRequest request) {
        return ResponseEntity.ok(taskService.createTask(request));
    }

    @GetMapping("/my-tasks")
    public ResponseEntity<List<TaskResponse>> getMyTasks() {
        return ResponseEntity.ok(taskService.getMyTasks());
    }

    @GetMapping("/created")
    public ResponseEntity<List<TaskResponse>> getCreatedTasks() {
        return ResponseEntity.ok(taskService.getCreatedTasks());
    }

    @PatchMapping("/{taskId}/status")
    public ResponseEntity<TaskResponse> updateTaskStatus(
            @PathVariable Long taskId,
            @RequestBody UpdateTaskStatusRequest request) {
        return ResponseEntity.ok(taskService.updateTaskStatus(taskId, request));
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long taskId,
            @RequestBody UpdateTaskRequest request) {
        return ResponseEntity.ok(taskService.updateTask(taskId, request));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<TaskResponse>> searchTasks(TaskSearchRequest request) {
        return ResponseEntity.ok(taskService.searchTasks(request));
    }
} 