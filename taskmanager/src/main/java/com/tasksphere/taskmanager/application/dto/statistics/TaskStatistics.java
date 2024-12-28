package com.tasksphere.taskmanager.application.dto.statistics;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class TaskStatistics {
    private long totalTasks;
    private long completedTasks;
    private long pendingTasks;
    private long overdueTasks;
    
    private Map<String, Long> tasksByStatus;      // Status bazlı dağılım
    private Map<String, Long> tasksByPriority;    // Öncelik bazlı dağılım
    private Map<String, Long> tasksByCategory;    // Kategori bazlı dağılım
    private Map<String, Long> mostUsedTags;       // En çok kullanılan etiketler
} 