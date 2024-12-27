package com.tasksphere.taskmanager.presentation.controller;

import com.tasksphere.taskmanager.application.dto.statistics.TaskStatistics;
import com.tasksphere.taskmanager.application.service.TaskStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
public class TaskStatisticsController {

    private final TaskStatisticsService statisticsService;

    @GetMapping("/my-tasks")
    public ResponseEntity<TaskStatistics> getMyTaskStatistics() {
        return ResponseEntity.ok(statisticsService.getMyTaskStatistics());
    }

    @GetMapping("/created-tasks")
    public ResponseEntity<TaskStatistics> getCreatedTaskStatistics() {
        return ResponseEntity.ok(statisticsService.getCreatedTaskStatistics());
    }
} 