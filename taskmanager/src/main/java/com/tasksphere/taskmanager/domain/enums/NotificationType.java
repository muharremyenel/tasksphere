package com.tasksphere.taskmanager.domain.enums;

public enum NotificationType {
    // Task Notifications
    TASK_ASSIGNED,
    TASK_STATUS_CHANGED,
    TASK_DUE_DATE_APPROACHING,  // 24 saat kala
    TASK_OVERDUE,              // Süresi geçmiş
    ADDED_AS_COLLABORATOR,
    REMOVED_AS_COLLABORATOR,
    
    // Team Notifications
    ADDED_TO_TEAM,
    REMOVED_FROM_TEAM,
    TEAM_LEAD_CHANGED,
    NEW_TEAM_MEMBER,           // Takıma yeni biri katıldığında
    
    // Comment Notifications
    NEW_COMMENT,              // Task'e yeni yorum eklendiğinde
    COMMENT_MENTIONED         // Yorumda mention edildiğinde (@user gibi)
} 