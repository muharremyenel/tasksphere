package com.tasksphere.taskmanager.infrastructure.persistence.repository;

import com.tasksphere.taskmanager.domain.entity.Task;
import com.tasksphere.taskmanager.domain.entity.User;
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
    List<Task> findByAssignedTo(User assignedTo);
    List<Task> findByAssignedToOrCollaboratorsContaining(User assignedTo, User collaborator);
    List<Task> findByCollaboratorsContaining(User user);
    List<Task> findByDueDateBetween(LocalDateTime start, LocalDateTime end);
    List<Task> findByDueDateBeforeAndStatusNot(LocalDateTime date, TaskStatus status);
} 