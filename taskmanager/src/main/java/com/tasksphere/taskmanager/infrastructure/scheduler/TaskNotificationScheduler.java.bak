package com.tasksphere.taskmanager.infrastructure.scheduler;

import com.tasksphere.taskmanager.application.service.NotificationService;
import com.tasksphere.taskmanager.domain.entity.Task;
import com.tasksphere.taskmanager.domain.enums.NotificationType;
import com.tasksphere.taskmanager.domain.enums.TaskStatus;
//import com.tasksphere.taskmanager.infrastructure.persistence.repository.NotificationRepository;
import com.tasksphere.taskmanager.infrastructure.persistence.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
//import java.util.Set;
//import java.util.HashSet;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskNotificationScheduler {

    private final TaskRepository taskRepository;
    //private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;

    @Scheduled(cron = "0 0 9 * * *")
    public void checkTasks() {
        LocalDateTime now = LocalDateTime.now();
        log.debug("Starting task notification check at: {}", now);
        
        // Overdue taskları kontrol et (sadece geçmiş tarihli taskler)
        List<Task> overdueTasks = taskRepository.findActiveTasksForNotification(
            now,
            now,  // twoDaysLater yerine now kullan
            List.of(TaskStatus.TODO, TaskStatus.IN_PROGRESS),
            NotificationType.TASK_OVERDUE,
            now.minusHours(24)
        );
        
        // Due soon taskları kontrol et (sadece gelecek tarihli taskler)
        List<Task> dueSoonTasks = taskRepository.findActiveTasksForNotification(
            now,
            now.plusDays(2),
            List.of(TaskStatus.TODO, TaskStatus.IN_PROGRESS),
            NotificationType.TASK_DUE_SOON,
            now.minusHours(24)
        );
        
        // Overdue bildirimleri oluştur (sadece geçmiş tarihli taskler için)
        for (Task task : overdueTasks) {
            if (task.getAssignedTo() != null && task.getDueDate().isBefore(now)) {
                createOverdueNotification(task);
            }
        }
        
        // Due soon bildirimleri oluştur (sadece gelecek tarihli taskler için)
        for (Task task : dueSoonTasks) {
            if (task.getAssignedTo() != null && 
                task.getDueDate().isAfter(now) && 
                task.getDueDate().isBefore(now.plusDays(2))) {
                createDueSoonNotification(task);
            }
        }
    }

    private void createDueSoonNotification(Task task) {
        log.debug("Creating due soon notification for task: {} (ID: {})", task.getTitle(), task.getId());
        notificationService.createNotification(
            task.getAssignedTo(),
            task,
            NotificationType.TASK_DUE_SOON,
            String.format("Task '%s' is due in %d days", 
                task.getTitle(),
                LocalDateTime.now().until(task.getDueDate(), ChronoUnit.DAYS))
        );
    }

    private void createOverdueNotification(Task task) {
        log.debug("Creating overdue notification for task: {} (ID: {})", task.getTitle(), task.getId());
        notificationService.createNotification(
            task.getAssignedTo(),
            task,
            NotificationType.TASK_OVERDUE,
            String.format("Task '%s' is overdue by %d days", 
                task.getTitle(),
                task.getDueDate().until(LocalDateTime.now(), ChronoUnit.DAYS))
        );
    }
} 