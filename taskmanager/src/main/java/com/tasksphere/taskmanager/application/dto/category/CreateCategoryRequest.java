package com.tasksphere.taskmanager.application.dto.category;

import lombok.Data;

@Data
public class CreateCategoryRequest {
    private String name;
    private String description;
    private String color;
} 