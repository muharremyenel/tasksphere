package com.tasksphere.taskmanager.application.service.impl;

import com.tasksphere.taskmanager.application.dto.task.CreateTaskRequest;
import com.tasksphere.taskmanager.application.dto.task.TaskResponse;
import com.tasksphere.taskmanager.application.dto.task.UpdateTaskStatusRequest;
import com.tasksphere.taskmanager.application.dto.task.UpdateTaskRequest;
import com.tasksphere.taskmanager.application.dto.task.TaskSearchRequest;
import com.tasksphere.taskmanager.application.service.TaskService;
import com.tasksphere.taskmanager.domain.entity.Task;
import com.tasksphere.taskmanager.domain.entity.User;
import com.tasksphere.taskmanager.domain.enums.TaskStatus;
import com.tasksphere.taskmanager.domain.exception.ResourceNotFoundException;
import com.tasksphere.taskmanager.infrastructure.persistence.repository.TaskRepository;
import com.tasksphere.taskmanager.infrastructure.persistence.repository.UserRepository;
import com.tasksphere.taskmanager.infrastructure.persistence.specification.TaskSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Override
    public TaskResponse createTask(CreateTaskRequest request) {
        User currentUser = getCurrentUser();
        User assignedTo = request.getAssignedToId() != null ?
                userRepository.findById(request.getAssignedToId())
                        .orElseThrow(() -> new ResourceNotFoundException("Assigned user not found")) : null;

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(TaskStatus.TODO)
                .priority(request.getPriority())
                .dueDate(request.getDueDate())
                .createdBy(currentUser)
                .assignedTo(assignedTo)
                .build();

        Task savedTask = taskRepository.save(task);
        return mapToTaskResponse(savedTask);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getMyTasks() {
        User currentUser = getCurrentUser();
        return taskRepository.findByAssignedToId(currentUser.getId())
                .stream()
                .map(this::mapToTaskResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getCreatedTasks() {
        User currentUser = getCurrentUser();
        return taskRepository.findByCreatedById(currentUser.getId())
                .stream()
                .map(this::mapToTaskResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TaskResponse updateTaskStatus(Long taskId, UpdateTaskStatusRequest request) {
        User currentUser = getCurrentUser();
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        // Sadece görevi oluşturan veya atanan kişi durumu güncelleyebilir
        if (!task.getCreatedBy().getId().equals(currentUser.getId()) && 
            (task.getAssignedTo() == null || !task.getAssignedTo().getId().equals(currentUser.getId()))) {
            throw new AccessDeniedException("You don't have permission to update this task");
        }

        task.setStatus(request.getStatus());
        Task updatedTask = taskRepository.save(task);
        return mapToTaskResponse(updatedTask);
    }

    @Override
    @Transactional
    public TaskResponse updateTask(Long taskId, UpdateTaskRequest request) {
        User currentUser = getCurrentUser();
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        // Sadece görevi oluşturan kişi güncelleyebilir
        if (!task.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You don't have permission to update this task");
        }

        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }
        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        }
        if (request.getAssignedToId() != null) {
            User assignedTo = userRepository.findById(request.getAssignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException("Assigned user not found"));
            task.setAssignedTo(assignedTo);
        }

        Task updatedTask = taskRepository.save(task);
        return mapToTaskResponse(updatedTask);
    }

    @Override
    @Transactional
    public void deleteTask(Long taskId) {
        User currentUser = getCurrentUser();
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        // Sadece görevi oluşturan kişi silebilir
        if (!task.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You don't have permission to delete this task");
        }

        taskRepository.delete(task);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> searchTasks(TaskSearchRequest request) {
        return taskRepository.findAll(TaskSpecification.withSearchCriteria(request))
                .stream()
                .map(this::mapToTaskResponse)
                .collect(Collectors.toList());
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private TaskResponse mapToTaskResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .createdAt(task.getCreatedAt())
                .dueDate(task.getDueDate())
                .createdBy(mapToUserSummary(task.getCreatedBy()))
                .assignedTo(task.getAssignedTo() != null ? mapToUserSummary(task.getAssignedTo()) : null)
                .build();
    }

    private TaskResponse.UserSummary mapToUserSummary(User user) {
        return TaskResponse.UserSummary.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
} 