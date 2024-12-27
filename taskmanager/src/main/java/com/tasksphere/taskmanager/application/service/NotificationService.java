package com.tasksphere.taskmanager.application.service;

import com.tasksphere.taskmanager.application.dto.notification.NotificationResponse;
import com.tasksphere.taskmanager.domain.entity.Task;
import com.tasksphere.taskmanager.domain.entity.User;
import com.tasksphere.taskmanager.domain.enums.NotificationType;

import java.util.List;

public interface NotificationService {
    void createNotification(User user, Task task, NotificationType type, String content);
    List<NotificationResponse> getMyNotifications();
    void markAsRead(Long notificationId);
    void markAllAsRead();
    long getUnreadCount();
    void deleteAllMyNotifications();
    void deleteNotification(Long notificationId);
} 