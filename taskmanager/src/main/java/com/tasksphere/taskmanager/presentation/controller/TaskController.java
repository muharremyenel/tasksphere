package com.tasksphere.taskmanager.presentation.controller;

import com.tasksphere.taskmanager.application.dto.task.CreateTaskRequest;
import com.tasksphere.taskmanager.application.dto.task.TaskResponse;
import com.tasksphere.taskmanager.application.dto.task.UpdateTaskStatusRequest;
import com.tasksphere.taskmanager.application.dto.task.UpdateTaskRequest;
import com.tasksphere.taskmanager.application.dto.task.TaskSearchRequest;
import com.tasksphere.taskmanager.application.dto.tag.TagResponse;
import com.tasksphere.taskmanager.application.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
//import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody CreateTaskRequest request) {
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
            @Valid @RequestBody UpdateTaskStatusRequest request) {
        return ResponseEntity.ok(taskService.updateTaskStatus(taskId, request));
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long taskId,
            @Valid @RequestBody UpdateTaskRequest request) {
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

    @PostMapping("/{taskId}/tags/{tagId}")
    public ResponseEntity<Void> addTagToTask(
            @PathVariable Long taskId,
            @PathVariable Long tagId) {
        taskService.addTagToTask(taskId, tagId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{taskId}/tags/{tagId}")
    public ResponseEntity<Void> removeTagFromTask(
            @PathVariable Long taskId,
            @PathVariable Long tagId) {
        taskService.removeTagFromTask(taskId, tagId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{taskId}/tags")
    public ResponseEntity<List<TagResponse>> getTaskTags(@PathVariable Long taskId) {
        return ResponseEntity.ok(taskService.getTaskTags(taskId));
    }

    @GetMapping("/collaborative")
    public ResponseEntity<List<TaskResponse>> getCollaborativeTasks() {
        return ResponseEntity.ok(taskService.getCollaborativeTasks());
    }

    @PostMapping("/{taskId}/collaborators/{userId}")
    public ResponseEntity<Void> addCollaborator(
            @PathVariable Long taskId,
            @PathVariable Long userId) {
        taskService.addCollaborator(taskId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{taskId}/collaborators/{userId}")
    public ResponseEntity<Void> removeCollaborator(
            @PathVariable Long taskId,
            @PathVariable Long userId) {
        taskService.removeCollaborator(taskId, userId);
        return ResponseEntity.noContent().build();
    }
} 