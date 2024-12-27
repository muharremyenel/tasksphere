package com.tasksphere.taskmanager.application.dto.category;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryResponse {
    private Long id;
    private String name;
    private String description;
    private String colorHex;
    private Long taskCount;
} 