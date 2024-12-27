package com.tasksphere.taskmanager.domain.enums;

public enum NotificationType {
    TASK_ASSIGNED,          // Task atandığında
    TASK_STATUS_CHANGED,    // Task durumu değiştiğinde
    TASK_DUE_SOON,         // Task tarihi yaklaştığında
    TASK_OVERDUE,          // Task tarihi geçtiğinde
    TASK_COMMENTED,        // Task'e yorum eklendiğinde
    ADDED_AS_COLLABORATOR  // Collaborator olarak eklendiğinde
} 