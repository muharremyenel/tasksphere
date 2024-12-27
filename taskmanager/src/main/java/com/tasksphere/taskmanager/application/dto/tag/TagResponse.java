package com.tasksphere.taskmanager.application.dto.tag;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TagResponse {
    private Long id;
    private String name;
    private String colorHex;
    private Long usageCount;
} 