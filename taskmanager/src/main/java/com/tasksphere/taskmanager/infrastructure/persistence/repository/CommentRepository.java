package com.tasksphere.taskmanager.infrastructure.persistence.repository;

import com.tasksphere.taskmanager.domain.entity.Comment;
import com.tasksphere.taskmanager.domain.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByTaskOrderByCreatedAtDesc(Task task);
} 