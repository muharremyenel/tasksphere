package com.tasksphere.taskmanager.application.service.impl;

import com.tasksphere.taskmanager.application.dto.notification.NotificationResponse;
import com.tasksphere.taskmanager.application.service.NotificationService;
import com.tasksphere.taskmanager.domain.entity.Notification;
import com.tasksphere.taskmanager.domain.entity.Task;
import com.tasksphere.taskmanager.domain.entity.User;
import com.tasksphere.taskmanager.domain.enums.NotificationType;
import com.tasksphere.taskmanager.domain.exception.ResourceNotFoundException;
import com.tasksphere.taskmanager.infrastructure.persistence.repository.NotificationRepository;
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
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Override
    public void createNotification(User user, Task task, NotificationType type, String content) {
        String title = generateTitle(type, task);
        
        Notification notification = Notification.builder()
                .title(title)
                .content(content)
                .user(user)
                .task(task)
                .type(type)
                .read(false)
                .build();
        
        notificationRepository.save(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getMyNotifications() {
        User currentUser = getCurrentUser();
        return notificationRepository.findByUserOrderByCreatedAtDesc(currentUser)
                .stream()
                .map(this::mapToNotificationResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void markAsRead(Long notificationId) {
        User currentUser = getCurrentUser();
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        if (!notification.getUser().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Notification not found");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Override
    public void markAllAsRead() {
        User currentUser = getCurrentUser();
        List<Notification> unreadNotifications = notificationRepository
                .findByUserAndReadOrderByCreatedAtDesc(currentUser, false);
        
        unreadNotifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(unreadNotifications);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount() {
        User currentUser = getCurrentUser();
        return notificationRepository.countByUserAndRead(currentUser, false);
    }

    @Override
    public void deleteAllMyNotifications() {
        User currentUser = getCurrentUser();
        List<Notification> notifications = notificationRepository.findByUser(currentUser);
        notificationRepository.deleteAll(notifications);
    }

    @Override
    public void deleteNotification(Long notificationId) {
        User currentUser = getCurrentUser();
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        if (!notification.getUser().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Notification not found");
        }

        notificationRepository.delete(notification);
    }

    private String generateTitle(NotificationType type, Task task) {
        return switch (type) {
            case TASK_ASSIGNED -> "New Task Assigned";
            case TASK_STATUS_CHANGED -> "Task Status Updated";
            case TASK_DUE_SOON -> "Task Due Date Approaching";
            case TASK_OVERDUE -> "Task Overdue";
            case TASK_COMMENTED -> "New Comment on Task";
            case ADDED_AS_COLLABORATOR -> "Added as Collaborator";
        };
    }

    private NotificationResponse mapToNotificationResponse(Notification notification) {
        NotificationResponse.TaskSummary taskSummary = null;
        if (notification.getTask() != null) {
            taskSummary = NotificationResponse.TaskSummary.builder()
                    .id(notification.getTask().getId())
                    .title(notification.getTask().getTitle())
                    .build();
        }

        return NotificationResponse.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .content(notification.getContent())
                .read(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .task(taskSummary)
                .build();
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
} 