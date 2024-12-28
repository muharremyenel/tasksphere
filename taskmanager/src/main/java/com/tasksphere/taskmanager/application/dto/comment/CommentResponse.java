package com.tasksphere.taskmanager.application.dto.comment;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class CommentResponse {
    private Long id;
    private String content;
    private UserSummary author;
    private LocalDateTime createdAt;

    @Data
    @Builder
    public static class UserSummary {
        private Long id;
        private String name;
        private String email;
    }
} 