package com.tasksphere.taskmanager.application.dto.task;

import com.tasksphere.taskmanager.domain.enums.TaskPriority;
import com.tasksphere.taskmanager.domain.enums.TaskStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskSearchRequest {
    private String searchTerm;        // Başlık veya açıklamada arama
    private TaskStatus status;        // Durum filtresi
    private TaskPriority priority;    // Öncelik filtresi
    private LocalDateTime startDate;  // Başlangıç tarihi
    private LocalDateTime endDate;    // Bitiş tarihi
    private Long categoryId;          // Kategori filtresi
    private Long assignedToId;        // Atanan kişi filtresi
} 