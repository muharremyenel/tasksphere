package com.tasksphere.taskmanager.application.service.impl;

import com.tasksphere.taskmanager.application.dto.tag.CreateTagRequest;
import com.tasksphere.taskmanager.application.dto.tag.TagResponse;
import com.tasksphere.taskmanager.application.dto.tag.UpdateTagRequest;
import com.tasksphere.taskmanager.application.service.TagService;
import com.tasksphere.taskmanager.domain.entity.Tag;
import com.tasksphere.taskmanager.domain.exception.ResourceNotFoundException;
import com.tasksphere.taskmanager.infrastructure.persistence.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;

    @Override
    public List<TagResponse> getTags() {
        return tagRepository.findAll().stream()
            .map(this::mapToTagResponse)
            .collect(Collectors.toList());
    }

    @Override
    public TagResponse createTag(CreateTagRequest request) {
        Tag tag = Tag.builder()
            .name(request.getName())
            .colorHex(request.getColorHex())
            .build();

        return mapToTagResponse(tagRepository.save(tag));
    }

    @Override
    public void deleteTag(Long id) {
        if (!tagRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tag not found");
        }
        tagRepository.deleteById(id);
    }

    @Override
    public TagResponse updateTag(Long id, UpdateTagRequest request) {
        Tag tag = tagRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Tag not found"));

        tag.setName(request.getName());
        tag.setColorHex(request.getColorHex());

        return mapToTagResponse(tagRepository.save(tag));
    }

    private TagResponse mapToTagResponse(Tag tag) {
        return TagResponse.builder()
            .id(tag.getId())
            .name(tag.getName())
            .colorHex(tag.getColorHex())
            .usageCount((long) tag.getTasks().size())
            .build();
    }
} 