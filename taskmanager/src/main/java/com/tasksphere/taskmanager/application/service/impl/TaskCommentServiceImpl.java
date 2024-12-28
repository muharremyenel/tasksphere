package com.tasksphere.taskmanager.application.service.impl;

import com.tasksphere.taskmanager.application.dto.comment.CommentResponse;
import com.tasksphere.taskmanager.application.dto.comment.CreateCommentRequest;
import com.tasksphere.taskmanager.application.service.TaskCommentService;
import com.tasksphere.taskmanager.domain.entity.Task;
import com.tasksphere.taskmanager.domain.entity.TaskComment;
import com.tasksphere.taskmanager.domain.entity.User;
import com.tasksphere.taskmanager.domain.exception.ResourceNotFoundException;
import com.tasksphere.taskmanager.infrastructure.persistence.repository.TaskCommentRepository;
import com.tasksphere.taskmanager.infrastructure.persistence.repository.TaskRepository;
import com.tasksphere.taskmanager.infrastructure.persistence.repository.UserRepository;
import com.tasksphere.taskmanager.domain.exception.UnauthorizedAccessException;
import com.tasksphere.taskmanager.domain.enums.NotificationType;
import com.tasksphere.taskmanager.application.service.NotificationService;
import com.tasksphere.taskmanager.domain.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskCommentServiceImpl implements TaskCommentService {

    private final TaskCommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Override
    public CommentResponse addComment(Long taskId, CreateCommentRequest request) {
        User currentUser = getCurrentUser();
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        // Yorum yapma yetkisi kontrolü
        if (!canAddComment(task, currentUser)) {
            throw new UnauthorizedAccessException("You don't have permission to comment on this task");
        }

        TaskComment comment = TaskComment.builder()
                .content(request.getContent())
                .task(task)
                .user(currentUser)
                .build();

        TaskComment savedComment = commentRepository.save(comment);

        // Task sahibine ve atanan kişiye bildirim gönder (eğer yorum yapan kişi değillerse)
        if (!task.getCreatedBy().equals(currentUser)) {
            notificationService.createNotification(
                task.getCreatedBy(),
                task,
                NotificationType.TASK_COMMENTED,
                String.format("%s commented on task: %s", currentUser.getName(), task.getTitle())
            );
        }

        if (task.getAssignedTo() != null && !task.getAssignedTo().equals(currentUser)) {
            notificationService.createNotification(
                task.getAssignedTo(),
                task,
                NotificationType.TASK_COMMENTED,
                String.format("%s commented on task: %s", currentUser.getName(), task.getTitle())
            );
        }

        return mapToCommentResponse(savedComment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponse> getTaskComments(Long taskId) {
        User currentUser = getCurrentUser();
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        // Yorumları görüntüleme yetkisi kontrolü
        if (!canViewComments(task, currentUser)) {
            throw new UnauthorizedAccessException("You don't have permission to view comments on this task");
        }

        return commentRepository.findByTaskId(taskId).stream()
                .map(this::mapToCommentResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteComment(Long commentId) {
        User currentUser = getCurrentUser();
        TaskComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        // Yorum silme yetkisi kontrolü
        if (!canDeleteComment(comment, currentUser)) {
            throw new UnauthorizedAccessException("You don't have permission to delete this comment");
        }

        commentRepository.delete(comment);
    }

    // Yetki kontrol metodları
    private boolean canAddComment(Task task, User user) {
        return user.getRole().equals(Role.ROLE_ADMIN) ||
               task.getCreatedBy().equals(user) ||
               task.getAssignedTo().equals(user) ||
               task.getCollaborators().contains(user);
    }

    private boolean canViewComments(Task task, User user) {
        return user.getRole().equals(Role.ROLE_ADMIN) ||
               task.getCreatedBy().equals(user) ||
               task.getAssignedTo().equals(user) ||
               task.getCollaborators().contains(user);
    }

    private boolean canDeleteComment(TaskComment comment, User user) {
        return user.getRole().equals(Role.ROLE_ADMIN) ||
               comment.getUser().equals(user) ||
               comment.getTask().getCreatedBy().equals(user);
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private CommentResponse mapToCommentResponse(TaskComment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .user(mapToUserSummary(comment.getUser()))
                .build();
    }

    private CommentResponse.UserSummary mapToUserSummary(User user) {
        return CommentResponse.UserSummary.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
} 