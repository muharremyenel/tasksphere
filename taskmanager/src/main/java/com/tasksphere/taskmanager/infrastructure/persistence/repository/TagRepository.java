package com.tasksphere.taskmanager.infrastructure.persistence.repository;

import com.tasksphere.taskmanager.domain.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
    boolean existsByName(String name);
} 