package com.tasksphere.taskmanager.application.dto.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateCommentRequest {
    @NotBlank(message = "Comment content is required")
    private String content;
} 