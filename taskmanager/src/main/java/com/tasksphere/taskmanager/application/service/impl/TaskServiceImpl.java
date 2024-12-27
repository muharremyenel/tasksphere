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
import com.tasksphere.taskmanager.domain.entity.Category;
import com.tasksphere.taskmanager.domain.enums.Role;
import com.tasksphere.taskmanager.domain.enums.TaskStatus;
import com.tasksphere.taskmanager.domain.exception.ResourceNotFoundException;
import com.tasksphere.taskmanager.domain.exception.UnauthorizedAccessException;
import com.tasksphere.taskmanager.infrastructure.persistence.repository.TaskRepository;
import com.tasksphere.taskmanager.infrastructure.persistence.repository.UserRepository;
import com.tasksphere.taskmanager.infrastructure.persistence.repository.TagRepository;
import com.tasksphere.taskmanager.infrastructure.persistence.repository.CategoryRepository;
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
    private final CategoryRepository categoryRepository;
    private final NotificationService notificationService;

    @Override
    public TaskResponse createTask(CreateTaskRequest request) {
        User currentUser = getCurrentUser();
        User assignedTo = request.getAssignedToId() != null ?
                userRepository.findById(request.getAssignedToId())
                        .orElseThrow(() -> new ResourceNotFoundException("Assigned user not found")) : null;

        // Kategori kontrolü
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(TaskStatus.TODO)
                .priority(request.getPriority())
                .dueDate(request.getDueDate())
                .createdBy(currentUser)
                .assignedTo(assignedTo)
                .category(category)
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
        return taskRepository.findByAssignedToOrCollaboratorsContaining(currentUser, currentUser)
                .stream()
                .map(this::mapToTaskResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getCreatedTasks() {
        User currentUser = getCurrentUser();
        if (!currentUser.getRole().equals(Role.ROLE_ADMIN)) {
            throw new UnauthorizedAccessException("Only admins can view all created tasks");
        }
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

        if (!canUpdateTaskStatus(task, currentUser)) {
            throw new UnauthorizedAccessException("You don't have permission to update this task's status");
        }

        task.setStatus(request.getStatus());
        return mapToTaskResponse(taskRepository.save(task));
    }

    @Override
    @Transactional
    public TaskResponse updateTask(Long taskId, UpdateTaskRequest request) {
        User currentUser = getCurrentUser();
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (!canUpdateTaskDetails(task, currentUser)) {
            throw new UnauthorizedAccessException("You don't have permission to update this task");
        }

        // Kategori kontrolü
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            task.setCategory(category);
        }

        // Temel alanları güncelle
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

        // Atanan kişiyi güncelle
        if (request.getAssignedToId() != null) {
            User assignedTo = userRepository.findById(request.getAssignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException("Assigned user not found"));
            
            // Eğer atanan kişi değiştiyse bildirim gönder
            if (!assignedTo.equals(task.getAssignedTo())) {
                task.setAssignedTo(assignedTo);
                notificationService.createNotification(
                    assignedTo,
                    task,
                    NotificationType.TASK_ASSIGNED,
                    String.format("You have been assigned to task: %s", task.getTitle())
                );
            }
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

        // Sadece görevi oluşturan kişi veya admin silebilir
        if (!task.getCreatedBy().equals(currentUser) && !currentUser.getRole().equals(Role.ROLE_ADMIN)) {
            throw new UnauthorizedAccessException("You don't have permission to delete this task");
        }

        taskRepository.delete(task);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> searchTasks(TaskSearchRequest request) {
        User currentUser = getCurrentUser();
        return taskRepository.findAll(TaskSpecification.buildSpecification(request, currentUser))
                .stream()
                .map(this::mapToTaskResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void addTagToTask(Long taskId, Long tagId) {
        User currentUser = getCurrentUser();
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
                
        if (!canUpdateTaskDetails(task, currentUser)) {
            throw new UnauthorizedAccessException("You don't have permission to modify this task");
        }

        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found"));
        
        if (task.getTags().contains(tag)) {
            throw new IllegalStateException("Tag is already added to this task");
        }
        
        task.getTags().add(tag);
        taskRepository.save(task);
    }

    @Override
    public void removeTagFromTask(Long taskId, Long tagId) {
        User currentUser = getCurrentUser();
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
                
        if (!canUpdateTaskDetails(task, currentUser)) {
            throw new UnauthorizedAccessException("You don't have permission to modify this task");
        }

        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found"));
        
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
        User currentUser = getCurrentUser();
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (!canUpdateTaskDetails(task, currentUser)) {
            throw new UnauthorizedAccessException("You don't have permission to add collaborators");
        }

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
                .category(mapToCategorySummary(task.getCategory()))
                .tags(task.getTags().stream()
                        .map(this::mapToTagSummary)
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

    private TaskResponse.CategorySummary mapToCategorySummary(Category category) {
        return TaskResponse.CategorySummary.builder()
                .id(category.getId())
                .name(category.getName())
                .colorHex(category.getColorHex())
                .build();
    }

    private TaskResponse.TagSummary mapToTagSummary(Tag tag) {
        return TaskResponse.TagSummary.builder()
                .id(tag.getId())
                .name(tag.getName())
                .colorHex(tag.getColorHex())
                .build();
    }

    // Yetki kontrolü için yardımcı metod
    private boolean hasTaskAccess(Task task, User user) {
        return user.getRole().equals(Role.ROLE_ADMIN) ||
               task.getCreatedBy().equals(user) ||
               task.getAssignedTo().equals(user) ||
               task.getCollaborators().contains(user);
    }

    // Task durumu güncelleme yetkisi kontrolü
    private boolean canUpdateTaskStatus(Task task, User user) {
        return user.getRole().equals(Role.ROLE_ADMIN) ||
               task.getAssignedTo().equals(user) ||
               task.getCollaborators().contains(user) ||
               task.getCreatedBy().equals(user);
    }

    // Task detayları güncelleme yetkisi kontrolü
    private boolean canUpdateTaskDetails(Task task, User user) {
        return user.getRole().equals(Role.ROLE_ADMIN) ||
               task.getCreatedBy().equals(user);
    }
} 