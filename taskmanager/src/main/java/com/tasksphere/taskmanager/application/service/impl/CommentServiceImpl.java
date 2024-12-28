package com.tasksphere.taskmanager.application.service.impl;

import com.tasksphere.taskmanager.application.dto.comment.CommentResponse;
import com.tasksphere.taskmanager.application.dto.comment.CreateCommentRequest;
import com.tasksphere.taskmanager.application.dto.comment.UpdateCommentRequest;
import com.tasksphere.taskmanager.application.service.CommentService;
import com.tasksphere.taskmanager.domain.entity.Comment;
import com.tasksphere.taskmanager.domain.entity.Task;
import com.tasksphere.taskmanager.domain.entity.User;
import com.tasksphere.taskmanager.domain.enums.Role;
import com.tasksphere.taskmanager.domain.exception.ResourceNotFoundException;
import com.tasksphere.taskmanager.domain.exception.UnauthorizedAccessException;
import com.tasksphere.taskmanager.application.dto.user.UserSummary;
import com.tasksphere.taskmanager.infrastructure.persistence.repository.CommentRepository;
import com.tasksphere.taskmanager.infrastructure.persistence.repository.TaskRepository;
import com.tasksphere.taskmanager.infrastructure.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Override
    public CommentResponse addComment(Long taskId, CreateCommentRequest request) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        User currentUser = getCurrentUser();

        if (!canCommentOnTask(task, currentUser)) {
            throw new UnauthorizedAccessException("You don't have permission to comment on this task");
        }

        Comment comment = Comment.builder()
            .content(request.getContent())
            .task(task)
            .author(currentUser)
            .build();

        return mapToCommentResponse(commentRepository.save(comment));
    }

    @Override
    public List<CommentResponse> getTaskComments(Long taskId) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        
        return commentRepository.findByTaskOrderByCreatedAtDesc(task).stream()
            .map(this::mapToCommentResponse)
            .collect(Collectors.toList());
    }

    @Override
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        
        User currentUser = getCurrentUser();
        if (!canDeleteComment(comment, currentUser)) {
            throw new UnauthorizedAccessException("You don't have permission to delete this comment");
        }

        commentRepository.delete(comment);
    }

    @Override
    public CommentResponse updateComment(Long commentId, UpdateCommentRequest request) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        
        User currentUser = getCurrentUser();
        if (!isCommentOwner(comment, currentUser)) {
            throw new UnauthorizedAccessException("You can only update your own comments");
        }

        comment.setContent(request.getContent());
        return mapToCommentResponse(commentRepository.save(comment));
    }

    private boolean canCommentOnTask(Task task, User user) {
        return user.getRole() == Role.ROLE_ADMIN ||
               task.getAssignedTo().equals(user) ||
               task.getCollaborators().contains(user);
    }

    private boolean canDeleteComment(Comment comment, User user) {
        return isCommentOwner(comment, user);
    }

    private boolean isCommentOwner(Comment comment, User user) {
        return comment.getAuthor().equals(user);
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private CommentResponse mapToCommentResponse(Comment comment) {
        return CommentResponse.builder()
            .id(comment.getId())
            .content(comment.getContent())
            .author(mapToUserSummary(comment.getAuthor()))
            .createdAt(comment.getCreatedAt())
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