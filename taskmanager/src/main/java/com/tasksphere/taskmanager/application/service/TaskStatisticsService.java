package com.tasksphere.taskmanager.application.service;

import com.tasksphere.taskmanager.application.dto.statistics.TaskStatistics;

public interface TaskStatisticsService {
    TaskStatistics getMyTaskStatistics();
    TaskStatistics getCreatedTaskStatistics();
} 