package com.tasksphere.taskmanager.application.service;

import com.tasksphere.taskmanager.application.dto.comment.CommentResponse;
import com.tasksphere.taskmanager.application.dto.comment.CreateCommentRequest;
import com.tasksphere.taskmanager.application.dto.comment.UpdateCommentRequest;
import java.util.List;

public interface CommentService {
    CommentResponse addComment(Long taskId, CreateCommentRequest request);
    List<CommentResponse> getTaskComments(Long taskId);
    CommentResponse updateComment(Long commentId, UpdateCommentRequest request);
    void deleteComment(Long commentId);
} 