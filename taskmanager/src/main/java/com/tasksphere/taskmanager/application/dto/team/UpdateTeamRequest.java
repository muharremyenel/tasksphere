package com.tasksphere.taskmanager.application.dto.team;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateTeamRequest {
    @NotBlank(message = "Team name is required")
    private String name;
    private String description;
    private Long teamLeadId;
} 