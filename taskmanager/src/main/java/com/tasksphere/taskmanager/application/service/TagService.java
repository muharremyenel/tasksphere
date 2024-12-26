package com.tasksphere.taskmanager.application.service;

import com.tasksphere.taskmanager.application.dto.tag.CreateTagRequest;
import com.tasksphere.taskmanager.application.dto.tag.TagResponse;

import java.util.List;

public interface TagService {
    TagResponse createTag(CreateTagRequest request);
    List<TagResponse> getAllTags();
    TagResponse getTagById(Long id);
    void deleteTag(Long id);
} 