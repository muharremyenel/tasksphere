package com.tasksphere.taskmanager.infrastructure.persistence.repository;

import com.tasksphere.taskmanager.domain.entity.Notification;
import com.tasksphere.taskmanager.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientOrderByCreatedAtDesc(User recipient);
    List<Notification> findByRecipientAndReadFalse(User recipient);
} 