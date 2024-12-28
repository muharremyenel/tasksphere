package com.tasksphere.taskmanager.application.dto.tag;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
public class CreateTagRequest {
    @NotBlank(message = "Tag name is required")
    private String name;

    @NotBlank(message = "Color hex code is required")
    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "Invalid color hex code")
    private String colorHex;
} 