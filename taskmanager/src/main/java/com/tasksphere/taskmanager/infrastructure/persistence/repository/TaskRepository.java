package com.tasksphere.taskmanager.infrastructure.persistence.repository;

import com.tasksphere.taskmanager.domain.entity.Task;
import com.tasksphere.taskmanager.domain.enums.TaskPriority;
import com.tasksphere.taskmanager.domain.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

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
} 