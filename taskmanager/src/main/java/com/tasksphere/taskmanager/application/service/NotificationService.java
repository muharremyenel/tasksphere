package com.tasksphere.taskmanager.application.service;

import com.tasksphere.taskmanager.application.dto.notification.NotificationResponse;
import com.tasksphere.taskmanager.domain.entity.Comment;
import com.tasksphere.taskmanager.domain.entity.Notification;
import com.tasksphere.taskmanager.domain.entity.Task;
import com.tasksphere.taskmanager.domain.entity.Team;
import com.tasksphere.taskmanager.domain.entity.User;
import com.tasksphere.taskmanager.domain.enums.NotificationType;
import com.tasksphere.taskmanager.domain.enums.TaskStatus;
import com.tasksphere.taskmanager.domain.exception.ResourceNotFoundException;
import com.tasksphere.taskmanager.infrastructure.persistence.repository.NotificationRepository;
import com.tasksphere.taskmanager.infrastructure.persistence.repository.TaskRepository;
import com.tasksphere.taskmanager.infrastructure.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    // Task Notifications
    public void notifyTaskAssignment(Task task) {
        if (task.getAssignedTo() != null) {
            createNotification(
                task.getAssignedTo(),
                task,
                NotificationType.TASK_ASSIGNED,
                String.format("You have been assigned to task: %s", task.getTitle())
            );
        }
    }

    public void notifyTaskStatusChange(Task task, TaskStatus oldStatus) {
        if (task.getCreatedBy() != null && !task.getCreatedBy().equals(task.getAssignedTo())) {
            createNotification(
                task.getCreatedBy(),
                task,
                NotificationType.TASK_STATUS_CHANGED,
                String.format("Task status changed from %s to %s: %s", 
                    oldStatus, task.getStatus(), task.getTitle())
            );
        }
    }

    // Team Notifications
    public void notifyTeamMemberAdded(Team team, User newMember) {
        // Yeni üyeye bildirim
        createNotification(
            newMember,
            null,
            NotificationType.ADDED_TO_TEAM,
            String.format("You have been added to team: %s", team.getName())
        );

        // Diğer üyelere bildirim
        team.getMembers().stream()
            .filter(member -> !member.equals(newMember))
            .forEach(member -> createNotification(
                member,
                null,
                NotificationType.NEW_TEAM_MEMBER,
                String.format("%s has joined the team", newMember.getName())
            ));
    }

    public void notifyTeamMemberRemoved(Team team, User removedMember) {
        createNotification(
            removedMember,
            null,
            NotificationType.REMOVED_FROM_TEAM,
            String.format("You have been removed from team: %s", team.getName())
        );
    }

    public void notifyTeamLeadChanged(Team team, User newTeamLead) {
        team.getMembers().forEach(member -> 
            createNotification(
                member,
                null,
                NotificationType.TEAM_LEAD_CHANGED,
                String.format("%s is now the team lead", newTeamLead.getName())
            )
        );
    }

    // Comment Notifications
    public void notifyNewComment(Comment comment) {
        Set<User> notifyUsers = comment.getTask().getCollaborators();
        notifyUsers.add(comment.getTask().getAssignedTo());
        notifyUsers.add(comment.getTask().getCreatedBy());
        notifyUsers.remove(comment.getAuthor()); // Yorum yazanı çıkar

        notifyUsers.forEach(user -> 
            createNotification(
                user,
                comment.getTask(),
                NotificationType.NEW_COMMENT,
                String.format("%s commented on task: %s", 
                    comment.getAuthor().getName(), 
                    comment.getTask().getTitle())
            )
        );
    }

    // Scheduled Notifications
    @Scheduled(cron = "0 0 * * * *") // Her saat başı
    public void checkDueDates() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneDayLater = now.plusDays(1);
        
        // Yaklaşan due date'ler
        List<Task> approachingTasks = taskRepository.findByDueDateBetween(now, oneDayLater);
        approachingTasks.forEach(task -> 
            createNotification(
                task.getAssignedTo(),
                task,
                NotificationType.TASK_DUE_DATE_APPROACHING,
                String.format("Task due in 24 hours: %s", task.getTitle())
            )
        );

        // Süresi geçmiş taskler
        List<Task> overdueTasks = taskRepository.findByDueDateBeforeAndStatusNot(now, TaskStatus.DONE);
        overdueTasks.forEach(task -> 
            createNotification(
                task.getAssignedTo(),
                task,
                NotificationType.TASK_OVERDUE,
                String.format("Task is overdue: %s", task.getTitle())
            )
        );
    }

    // Core Notification Methods
    public void createNotification(User recipient, Task task, NotificationType type, String message) {
        Notification notification = Notification.builder()
            .recipient(recipient)
            .task(task)
            .type(type)
            .message(message)
            .read(false)
            .createdAt(LocalDateTime.now())
            .build();

        notificationRepository.save(notification);
    }

    public List<NotificationResponse> getUserNotifications() {
        User currentUser = getCurrentUser();
        return notificationRepository.findByRecipientOrderByCreatedAtDesc(currentUser)
            .stream()
            .map(this::mapToNotificationResponse)
            .collect(Collectors.toList());
    }

    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        
        if (!notification.getRecipient().equals(getCurrentUser())) {
            throw new ResourceNotFoundException("Notification not found");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private NotificationResponse mapToNotificationResponse(Notification notification) {
        return NotificationResponse.builder()
            .id(notification.getId())
            .message(notification.getMessage())
            .type(notification.getType())
            .taskId(notification.getTask() != null ? notification.getTask().getId() : null)
            .read(notification.isRead())
            .createdAt(notification.getCreatedAt())
            .build();
    }

    public void markAllAsRead() {
        User currentUser = getCurrentUser();
        List<Notification> notifications = notificationRepository.findByRecipientAndReadFalse(currentUser);
        
        notifications.forEach(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
    }

    public void deleteNotification(Long id) {
        Notification notification = notificationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        
        if (!notification.getRecipient().equals(getCurrentUser())) {
            throw new ResourceNotFoundException("Notification not found");
        }

        notificationRepository.delete(notification);
    }

    // Collaborator notifications
    public void notifyCollaboratorAdded(Task task, User collaborator) {
        createNotification(
            collaborator,
            task,
            NotificationType.ADDED_AS_COLLABORATOR,
            String.format("You have been added as a collaborator to task: %s", task.getTitle())
        );
    }

    public void notifyCollaboratorRemoved(Task task, User collaborator) {
        createNotification(
            collaborator,
            task,
            NotificationType.REMOVED_AS_COLLABORATOR,
            String.format("You have been removed as a collaborator from task: %s", task.getTitle())
        );
    }
} 