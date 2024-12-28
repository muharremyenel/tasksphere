package com.tasksphere.taskmanager.presentation.controller;

import com.tasksphere.taskmanager.application.dto.comment.CommentResponse;
import com.tasksphere.taskmanager.application.dto.comment.CreateCommentRequest;
import com.tasksphere.taskmanager.application.dto.comment.UpdateCommentRequest;
import com.tasksphere.taskmanager.application.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks/{taskId}/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<List<CommentResponse>> getTaskComments(@PathVariable Long taskId) {
        return ResponseEntity.ok(commentService.getTaskComments(taskId));
    }

    @PostMapping
    public ResponseEntity<CommentResponse> addComment(
        @PathVariable Long taskId,
        @Valid @RequestBody CreateCommentRequest request
    ) {
        return ResponseEntity.ok(commentService.addComment(taskId, request));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
        @PathVariable Long taskId,
        @PathVariable Long commentId,
        @Valid @RequestBody UpdateCommentRequest request
    ) {
        return ResponseEntity.ok(commentService.updateComment(commentId, request));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
        @PathVariable Long taskId,
        @PathVariable Long commentId
    ) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
} 