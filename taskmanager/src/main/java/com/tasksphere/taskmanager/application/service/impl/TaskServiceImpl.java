package com.tasksphere.taskmanager.application.service.impl;

import com.tasksphere.taskmanager.application.dto.task.*;
import com.tasksphere.taskmanager.application.dto.tag.TagResponse;
import com.tasksphere.taskmanager.application.dto.comment.*;
import com.tasksphere.taskmanager.application.dto.statistics.TaskStatisticsResponse;
import com.tasksphere.taskmanager.application.service.TaskService;
import com.tasksphere.taskmanager.application.service.NotificationService;
import com.tasksphere.taskmanager.domain.entity.*;
import com.tasksphere.taskmanager.domain.enums.*;
import com.tasksphere.taskmanager.domain.exception.*;
import com.tasksphere.taskmanager.infrastructure.persistence.repository.*;
import com.tasksphere.taskmanager.infrastructure.persistence.specification.TaskSpecification;
import com.tasksphere.taskmanager.application.dto.task.TaskResponse.UserSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final CategoryRepository categoryRepository;
    private final CommentRepository commentRepository;
    private final NotificationService notificationService;

    @Override
    public List<TaskResponse> getTasks() {
        User currentUser = getCurrentUser();
        List<Task> tasks;
        
        if (currentUser.getRole() == Role.ROLE_ADMIN) {
            tasks = taskRepository.findAll();
        } else {
            tasks = taskRepository.findByAssignedToOrCollaboratorsContaining(currentUser, currentUser);
        }
        
        return tasks.stream()
            .map(this::mapToTaskResponse)
            .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public TaskResponse createTask(CreateTaskRequest request) {
        User currentUser = getCurrentUser();
        User assignedTo = null;
        Category category = null;
        Set<Tag> tags = new HashSet<>();

        // Assigned user check
        if (request.getAssignedToId() != null) {
            assignedTo = userRepository.findById(request.getAssignedToId())
                .orElseThrow(() -> new ResourceNotFoundException("Assigned user not found"));
            
            // Team check
            if (!isInSameTeam(currentUser, assignedTo)) {
                throw new UnauthorizedAccessException("Cannot assign task to user from different team");
            }
        }

        // Category check
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        }

        // Tags check
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            tags = new HashSet<>(tagRepository.findAllById(request.getTagIds()));
        }

        Task task = Task.builder()
            .title(request.getTitle())
            .description(request.getDescription())
            .status(request.getStatus() != null ? request.getStatus() : TaskStatus.TODO)
            .priority(request.getPriority())
            .dueDate(request.getDueDate())
            .createdBy(currentUser)
            .assignedTo(assignedTo)
            .category(category)
            .tags(tags)
            .build();

        Task savedTask = taskRepository.save(task);

        if (assignedTo != null) {
            notificationService.notifyTaskAssignment(savedTask);
        }

        return mapToTaskResponse(savedTask);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public TaskResponse updateTask(Long id, UpdateTaskRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (!canUpdateTaskDetails(task, getCurrentUser())) {
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
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        // Sadece görevi oluşturan kişi veya admin silebilir
        if (!task.getCreatedBy().equals(getCurrentUser()) && !getCurrentUser().getRole().equals(Role.ROLE_ADMIN)) {
            throw new UnauthorizedAccessException("You don't have permission to delete this task");
        }

        taskRepository.delete(task);
    }

    @Override
    public TaskResponse updateTaskStatus(Long taskId, UpdateTaskStatusRequest request) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        
        User currentUser = getCurrentUser();
        if (!canUpdateTaskStatus(task, currentUser)) {
            throw new UnauthorizedAccessException("You can only update your own tasks");
        }

        TaskStatus oldStatus = task.getStatus();
        task.setStatus(request.getStatus());
        Task updatedTask = taskRepository.save(task);

        // Notify about status change
        notificationService.notifyTaskStatusChange(updatedTask, oldStatus);

        return mapToTaskResponse(updatedTask);
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
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!isInSameTeam(task.getAssignedTo(), user)) {
            throw new UnauthorizedAccessException("Can only add collaborators from the same team");
        }

        task.getCollaborators().add(user);
        taskRepository.save(task);

        notificationService.notifyCollaboratorAdded(task, user);
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

    @Override
    public TaskStatisticsResponse getStatistics() {
        User currentUser = getCurrentUser();
        List<Task> tasks = currentUser.getRole() == Role.ROLE_ADMIN 
            ? taskRepository.findAll()
            : taskRepository.findByAssignedTo(currentUser);

        return TaskStatisticsResponse.builder()
            .totalTasks(tasks.size())
            .todoTasks(tasks.stream().filter(t -> t.getStatus() == TaskStatus.TODO).count())
            .inProgressTasks(tasks.stream().filter(t -> t.getStatus() == TaskStatus.IN_PROGRESS).count())
            .completedTasks(tasks.stream().filter(t -> t.getStatus() == TaskStatus.DONE).count())
            .overdueTasks(tasks.stream().filter(t -> t.getDueDate().isBefore(LocalDateTime.now())).count())
            .build();
    }

    @Override
    public CommentResponse addComment(Long taskId, CreateCommentRequest request) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        User currentUser = getCurrentUser();

        // Yorum yapma yetkisi kontrolü
        if (!canCommentOnTask(task, currentUser)) {
            throw new UnauthorizedAccessException("You don't have permission to comment on this task");
        }

        Comment comment = Comment.builder()
            .content(request.getContent())
            .task(task)
            .author(currentUser)
            .build();

        Comment savedComment = commentRepository.save(comment);
        return mapToCommentResponse(savedComment);
    }

    @Override
    public List<CommentResponse> getTaskComments(Long taskId) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        
        return commentRepository.findByTaskOrderByCreatedAtDesc(task).stream()
            .map(this::mapToCommentResponse)
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
            .category(task.getCategory() != null ? mapToCategorySummary(task.getCategory()) : null)
            .tags(task.getTags().stream().map(this::mapToTagSummary).collect(Collectors.toSet()))
            .collaborators(task.getCollaborators().stream().map(this::mapToUserSummary).collect(Collectors.toSet()))
            .recentComments(task.getComments().stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
                .limit(5)
                .map(this::mapToCommentResponse)
                .collect(Collectors.toList()))
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
        if (category == null) return null;
        
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

    private boolean canUpdateTaskDetails(Task task, User user) {
        return user.getRole() == Role.ROLE_ADMIN ||
               task.getCreatedBy().equals(user);
    }

    private boolean canCommentOnTask(Task task, User user) {
        return user.getRole() == Role.ROLE_ADMIN ||
               task.getAssignedTo().equals(user) ||
               task.getCollaborators().contains(user);
    }

    private CommentResponse mapToCommentResponse(Comment comment) {
        return CommentResponse.builder()
            .id(comment.getId())
            .content(comment.getContent())
            .author(CommentResponse.UserSummary.builder()
                .id(comment.getAuthor().getId())
                .name(comment.getAuthor().getName())
                .email(comment.getAuthor().getEmail())
                .build())
            .createdAt(comment.getCreatedAt())
            .build();
    }

    private boolean isInSameTeam(User user1, User user2) {
        return user1.getTeam() != null && 
               user2.getTeam() != null && 
               user1.getTeam().getId().equals(user2.getTeam().getId());
    }

    private boolean canViewTask(Task task, User user) {
        return user.getRole() == Role.ROLE_ADMIN ||
               task.getCreatedBy().equals(user) ||
               task.getAssignedTo().equals(user) ||
               task.getCollaborators().contains(user) ||
               isInSameTeam(task.getAssignedTo(), user);
    }

    private boolean canUpdateTaskStatus(Task task, User user) {
        return user.getRole() == Role.ROLE_ADMIN ||
               task.getAssignedTo().equals(user) ||
               task.getCollaborators().contains(user);
    }
} 