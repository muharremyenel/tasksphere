package com.tasksphere.taskmanager.application.dto.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCommentRequest {
    @NotBlank(message = "Comment content is required")
    private String content;
} 