package com.tasksphere.taskmanager.infrastructure.persistence.repository;

import com.tasksphere.taskmanager.domain.entity.Task;
import com.tasksphere.taskmanager.domain.entity.User;
import com.tasksphere.taskmanager.domain.enums.TaskPriority;
import com.tasksphere.taskmanager.domain.enums.TaskStatus;
import com.tasksphere.taskmanager.domain.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Collection;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {
    List<Task> findByCreatedById(Long userId);
    List<Task> findByAssignedToId(Long userId);

    @Query("SELECT t FROM Task t " +
           "WHERE (:searchTerm IS NULL OR " +
           "      LOWER(t.title) LIKE LOWER(:searchPattern) OR " +
           "      LOWER(t.description) LIKE LOWER(:searchPattern)) " +
           "AND (:status IS NULL OR t.status = :status) " +
           "AND (:priority IS NULL OR t.priority = :priority) " +
           "AND (:startDate IS NULL OR t.createdAt >= :startDate) " +
           "AND (:endDate IS NULL OR t.createdAt <= :endDate) " +
           "AND (:categoryId IS NULL OR t.category.id = :categoryId) " +
           "AND (:assignedToId IS NULL OR t.assignedTo.id = :assignedToId)")
    List<Task> searchTasks(
            @Param("searchTerm") String searchTerm,
            @Param("searchPattern") String searchPattern,
            @Param("status") TaskStatus status,
            @Param("priority") TaskPriority priority,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("categoryId") Long categoryId,
            @Param("assignedToId") Long assignedToId
    );

    List<Task> findByCollaboratorsContaining(User user);

    List<Task> findByDueDateBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT t FROM Task t WHERE t.dueDate < :now AND t.status IN :statuses")
    List<Task> findByDueDateBeforeAndStatus(
        @Param("now") LocalDateTime now,
        @Param("statuses") TaskStatus... statuses
    );

    List<Task> findByDueDateBetweenAndStatusIn(
        LocalDateTime start, 
        LocalDateTime end, 
        TaskStatus... statuses
    );

    List<Task> findByDueDateBeforeAndStatusIn(
        LocalDateTime date,
        Collection<TaskStatus> statuses
    );

    @Query("SELECT DISTINCT t FROM Task t WHERE t.dueDate BETWEEN :start AND :end AND t.status IN :statuses")
    List<Task> findDistinctByDueDateBetweenAndStatusIn(
        @Param("start") LocalDateTime start, 
        @Param("end") LocalDateTime end,
        @Param("statuses") TaskStatus... statuses
    );

    @Query("SELECT DISTINCT t FROM Task t WHERE t.dueDate < :date AND t.status IN :statuses")
    List<Task> findDistinctByDueDateBeforeAndStatusIn(
        @Param("date") LocalDateTime date,
        @Param("statuses") Collection<TaskStatus> statuses
    );

    @Query("SELECT DISTINCT t FROM Task t " +
           "WHERE t.status IN :statuses " +
           "AND (t.dueDate < :now OR (t.dueDate BETWEEN :now AND :twoDaysLater)) " +
           "AND t.createdAt > :twentyFourHoursAgo " +
           "AND NOT EXISTS (" +
           "    SELECT 1 FROM Notification n " +
           "    WHERE n.task = t " +
           "    AND n.type = :notificationType " +
           "    AND n.createdAt > :twentyFourHoursAgo" +
           ")")
    List<Task> findActiveTasksForNotification(
        @Param("now") LocalDateTime now,
        @Param("twoDaysLater") LocalDateTime twoDaysLater,
        @Param("statuses") Collection<TaskStatus> statuses,
        @Param("notificationType") NotificationType notificationType,
        @Param("twentyFourHoursAgo") LocalDateTime twentyFourHoursAgo
    );
} 