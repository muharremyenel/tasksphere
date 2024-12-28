package com.tasksphere.taskmanager.application.dto.team;

import com.tasksphere.taskmanager.application.dto.user.UserSummary;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class TeamResponse {
    private Long id;
    private String name;
    private String description;
    private UserSummary teamLead;
    private Set<UserSummary> members;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 