package com.tasksphere.taskmanager.application.service.impl;

import com.tasksphere.taskmanager.application.dto.statistics.TaskStatistics;
import com.tasksphere.taskmanager.application.service.TaskStatisticsService;
import com.tasksphere.taskmanager.domain.entity.Task;
import com.tasksphere.taskmanager.domain.entity.User;
import com.tasksphere.taskmanager.domain.enums.TaskStatus;
import com.tasksphere.taskmanager.infrastructure.persistence.repository.TaskRepository;
import com.tasksphere.taskmanager.infrastructure.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskStatisticsServiceImpl implements TaskStatisticsService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Override
    public TaskStatistics getMyTaskStatistics() {
        User currentUser = getCurrentUser();
        List<Task> tasks = taskRepository.findByAssignedToId(currentUser.getId());
        return buildTaskStatistics(tasks);
    }

    @Override
    public TaskStatistics getCreatedTaskStatistics() {
        User currentUser = getCurrentUser();
        List<Task> tasks = taskRepository.findByCreatedById(currentUser.getId());
        return buildTaskStatistics(tasks);
    }

    private TaskStatistics buildTaskStatistics(List<Task> tasks) {
        long totalTasks = tasks.size();
        long completedTasks = tasks.stream()
                .filter(task -> task.getStatus() == TaskStatus.DONE)
                .count();
        long pendingTasks = tasks.stream()
                .filter(task -> task.getStatus() != TaskStatus.DONE)
                .count();
        long overdueTasks = tasks.stream()
                .filter(task -> task.getDueDate() != null && 
                        task.getDueDate().isBefore(LocalDateTime.now()) &&
                        task.getStatus() != TaskStatus.DONE)
                .count();

        Map<String, Long> tasksByStatus = tasks.stream()
                .collect(Collectors.groupingBy(
                        task -> task.getStatus().name(),
                        Collectors.counting()
                ));

        Map<String, Long> tasksByPriority = tasks.stream()
                .collect(Collectors.groupingBy(
                        task -> task.getPriority().name(),
                        Collectors.counting()
                ));

        Map<String, Long> tasksByCategory = tasks.stream()
                .filter(task -> task.getCategory() != null)
                .collect(Collectors.groupingBy(
                        task -> task.getCategory().getName(),
                        Collectors.counting()
                ));

        Map<String, Long> mostUsedTags = tasks.stream()
                .flatMap(task -> task.getTags().stream())
                .collect(Collectors.groupingBy(
                        tag -> tag.getName(),
                        Collectors.counting()
                ));

        return TaskStatistics.builder()
                .totalTasks(totalTasks)
                .completedTasks(completedTasks)
                .pendingTasks(pendingTasks)
                .overdueTasks(overdueTasks)
                .tasksByStatus(tasksByStatus)
                .tasksByPriority(tasksByPriority)
                .tasksByCategory(tasksByCategory)
                .mostUsedTags(mostUsedTags)
                .build();
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
} 