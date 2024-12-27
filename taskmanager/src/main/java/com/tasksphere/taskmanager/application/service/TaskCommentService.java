package com.tasksphere.taskmanager.application.service;

import com.tasksphere.taskmanager.application.dto.comment.CommentResponse;
import com.tasksphere.taskmanager.application.dto.comment.CreateCommentRequest;

import java.util.List;

public interface TaskCommentService {
    CommentResponse addComment(Long taskId, CreateCommentRequest request);
    List<CommentResponse> getTaskComments(Long taskId);
    void deleteComment(Long commentId);
} 