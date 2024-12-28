package com.tasksphere.taskmanager.application.service;

import com.tasksphere.taskmanager.application.dto.tag.CreateTagRequest;
import com.tasksphere.taskmanager.application.dto.tag.TagResponse;
import com.tasksphere.taskmanager.application.dto.tag.UpdateTagRequest;
import java.util.List;

public interface TagService {
    List<TagResponse> getTags();
    TagResponse createTag(CreateTagRequest request);
    void deleteTag(Long id);
    TagResponse updateTag(Long id, UpdateTagRequest request);
} 