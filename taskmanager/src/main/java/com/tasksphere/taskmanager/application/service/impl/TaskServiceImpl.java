package com.tasksphere.taskmanager.application.service.impl;

import com.tasksphere.taskmanager.application.dto.task.CreateTaskRequest;
import com.tasksphere.taskmanager.application.dto.task.TaskResponse;
import com.tasksphere.taskmanager.application.dto.task.UpdateTaskStatusRequest;
import com.tasksphere.taskmanager.application.dto.task.UpdateTaskRequest;
import com.tasksphere.taskmanager.application.dto.task.TaskSearchRequest;
import com.tasksphere.taskmanager.application.dto.tag.TagResponse;
import com.tasksphere.taskmanager.application.service.TaskService;
import com.tasksphere.taskmanager.domain.entity.Task;
import com.tasksphere.taskmanager.domain.entity.User;
import com.tasksphere.taskmanager.domain.entity.Tag;
import com.tasksphere.taskmanager.domain.enums.Role;
import com.tasksphere.taskmanager.domain.enums.TaskStatus;
import com.tasksphere.taskmanager.domain.exception.ResourceNotFoundException;
import com.tasksphere.taskmanager.infrastructure.persistence.repository.TaskRepository;
import com.tasksphere.taskmanager.infrastructure.persistence.repository.UserRepository;
import com.tasksphere.taskmanager.infrastructure.persistence.repository.TagRepository;
import com.tasksphere.taskmanager.infrastructure.persistence.specification.TaskSpecification;
import com.tasksphere.taskmanager.application.service.NotificationService;
import com.tasksphere.taskmanager.domain.enums.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final NotificationService notificationService;

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

        if (assignedTo != null) {
            notificationService.createNotification(
                assignedTo,
                savedTask,
                NotificationType.TASK_ASSIGNED,
                String.format("You have been assigned to task: %s", request.getTitle())
            );
        }

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

        // Task'in sahibine bildirim gönder
        if (!task.getCreatedBy().getId().equals(currentUser.getId())) {
            notificationService.createNotification(
                task.getCreatedBy(),
                updatedTask,
                NotificationType.TASK_STATUS_CHANGED,
                String.format("Task status changed to %s by %s", 
                    request.getStatus(),
                    currentUser.getName())
            );
        }

        return mapToTaskResponse(updatedTask);
    }

    @Override
    @Transactional
    public TaskResponse updateTask(Long taskId, UpdateTaskRequest request) {
        User currentUser = getCurrentUser();
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        // Admin her task'i güncelleyebilir
        if (currentUser.getRole().equals(Role.ROLE_ADMIN)) {
            updateTaskFields(task, request);
            Task updatedTask = taskRepository.save(task);
            return mapToTaskResponse(updatedTask);
        }

        // Admin değilse, sadece kendi oluşturduğu task'leri güncelleyebilir
        if (!task.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You don't have permission to update this task");
        }

        updateTaskFields(task, request);
        Task updatedTask = taskRepository.save(task);
        return mapToTaskResponse(updatedTask);
    }

    // Task alanlarını güncelleyen yardımcı metod
    private void updateTaskFields(Task task, UpdateTaskRequest request) {
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

    @Override
    public void addTagToTask(Long taskId, Long tagId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
                
        if (!task.getCreatedBy().getEmail().equals(getCurrentUser().getEmail()) && 
            !getCurrentUser().getRole().equals(Role.ROLE_ADMIN)) {
            throw new AccessDeniedException("You don't have permission to modify this task");
        }

        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found"));
        
        // Tag zaten ekli mi kontrolü
        if (task.getTags().contains(tag)) {
            throw new IllegalStateException("Tag is already added to this task");
        }
        
        task.getTags().add(tag);
        taskRepository.save(task);
    }

    @Override
    public void removeTagFromTask(Long taskId, Long tagId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
                
        if (!task.getCreatedBy().getEmail().equals(getCurrentUser().getEmail()) && 
            !getCurrentUser().getRole().equals(Role.ROLE_ADMIN)) {
            throw new AccessDeniedException("You don't have permission to modify this task");
        }

        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found"));
        
        // Tag task'ta var mı kontrolü
        if (!task.getTags().contains(tag)) {
            throw new IllegalStateException("Tag is not added to this task");
        }
        
        task.getTags().remove(tag);
        taskRepository.save(task);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TagResponse> getTaskTags(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        
        return new ArrayList<>(task.getTags()).stream()
                .map(tag -> TagResponse.builder()
                        .id(tag.getId())
                        .name(tag.getName())
                        .colorHex(tag.getColorHex())
                        .usageCount((long) tag.getTasks().size())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void addCollaborator(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                
        task.getCollaborators().add(user);
        taskRepository.save(task);

        // Yeni collaborator'a bildirim gönder
        notificationService.createNotification(
            user,
            task,
            NotificationType.ADDED_AS_COLLABORATOR,
            String.format("You have been added as a collaborator to task: %s", task.getTitle())
        );
    }

    @Override
    public void removeCollaborator(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                
        task.getCollaborators().remove(user);
        taskRepository.save(task);
    }

    @Override
    public List<TaskResponse> getCollaborativeTasks() {
        User currentUser = getCurrentUser();
        return taskRepository.findByCollaboratorsContaining(currentUser)
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
                .collaborators(task.getCollaborators().stream()
                        .map(this::mapToUserSummary)
                        .collect(Collectors.toSet()))
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