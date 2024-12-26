package com.tasksphere.taskmanager.application.dto.tag;

import lombok.Data;

@Data
public class CreateTagRequest {
    private String name;
    private String colorHex;
} 