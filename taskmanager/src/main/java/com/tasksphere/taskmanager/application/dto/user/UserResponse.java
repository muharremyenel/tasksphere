package com.tasksphere.taskmanager.application.dto.user;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private String role;
    private TeamSummary team;
    private LocalDateTime createdAt;

    @Data
    @Builder
    public static class TeamSummary {
        private Long id;
        private String name;
    }
} 