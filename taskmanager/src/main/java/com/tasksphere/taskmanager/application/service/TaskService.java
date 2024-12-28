package com.tasksphere.taskmanager.application.service;

import com.tasksphere.taskmanager.application.dto.task.CreateTaskRequest;
import com.tasksphere.taskmanager.application.dto.task.TaskResponse;
import com.tasksphere.taskmanager.application.dto.task.UpdateTaskStatusRequest;
import com.tasksphere.taskmanager.application.dto.task.UpdateTaskRequest;
import com.tasksphere.taskmanager.application.dto.task.TaskSearchRequest;
import com.tasksphere.taskmanager.application.dto.tag.TagResponse;
import com.tasksphere.taskmanager.application.dto.statistics.TaskStatisticsResponse;
import com.tasksphere.taskmanager.application.dto.comment.CreateCommentRequest;
import com.tasksphere.taskmanager.application.dto.comment.CommentResponse;

import java.util.List;

public interface TaskService {
    List<TaskResponse> getTasks();
    TaskResponse createTask(CreateTaskRequest request);
    List<TaskResponse> getMyTasks();
    List<TaskResponse> getCreatedTasks();
    TaskResponse updateTaskStatus(Long taskId, UpdateTaskStatusRequest request);
    TaskResponse updateTask(Long taskId, UpdateTaskRequest request);
    void deleteTask(Long taskId);
    List<TaskResponse> searchTasks(TaskSearchRequest request);
    void addTagToTask(Long taskId, Long tagId);
    void removeTagFromTask(Long taskId, Long tagId);
    List<TagResponse> getTaskTags(Long taskId);
    void addCollaborator(Long taskId, Long userId);
    void removeCollaborator(Long taskId, Long userId);
    List<TaskResponse> getCollaborativeTasks();
    TaskStatisticsResponse getStatistics();
    CommentResponse addComment(Long taskId, CreateCommentRequest request);
    List<CommentResponse> getTaskComments(Long taskId);
} 