package com.tasksphere.taskmanager.presentation.controller;

import com.tasksphere.taskmanager.application.dto.comment.CommentResponse;
import com.tasksphere.taskmanager.application.dto.comment.CreateCommentRequest;
import com.tasksphere.taskmanager.application.service.TaskCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks/{taskId}/comments")
@RequiredArgsConstructor
public class TaskCommentController {

    private final TaskCommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long taskId,
            @RequestBody CreateCommentRequest request) {
        return ResponseEntity.ok(commentService.addComment(taskId, request));
    }

    @GetMapping
    public ResponseEntity<List<CommentResponse>> getTaskComments(@PathVariable Long taskId) {
        return ResponseEntity.ok(commentService.getTaskComments(taskId));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long taskId,
            @PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
} 